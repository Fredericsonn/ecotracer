#!/usr/bin/env python3
from pyspark.sql import SparkSession
from pyspark.sql.functions import *
from pyspark.sql.window import Window
import json
import logging
import sys

# Configuration logging
logging.basicConfig(
    level=logging.INFO,
    format='%(message)s',
    handlers=[logging.StreamHandler(sys.stdout)]
)
logger = logging.getLogger(__name__)

# Supprimer les logs Spark superflus
spark = SparkSession.builder \
    .appName("Energy_ETL_Bronze_To_Silver") \
    .master("local[4]") \
    .config("spark.sql.adaptive.enabled", "true") \
    .config("spark.sql.shuffle.partitions", "8") \
    .config("spark.sql.adaptive.coalescePartitions.enabled", "true") \
    .config("spark.ui.showConsoleProgress", "false") \
    .config("spark.logConf", "false") \
    .getOrCreate()

spark.sparkContext.setLogLevel("ERROR")  

BRONZE_PATH = "hdfs://172.31.253.133:9000/bronze"
SILVER_PATH = "hdfs://172.31.253.133:9000/silver/energy"
CHECKPOINT_PATH = "hdfs://172.31.253.133:9000/silver/checkpoints/energy_checkpoint.json"
CO2_FACTOR = 0.053


def get_fs():
    return spark._jvm.org.apache.hadoop.fs.FileSystem.get(spark._jsc.hadoopConfiguration())

# -------------------------------------------------------
# Checkpoint functions 
# -------------------------------------------------------
def load_checkpoint():
    checkpoint = {}
    try:
        fs = get_fs()
        cp_path = spark._jvm.org.apache.hadoop.fs.Path(CHECKPOINT_PATH)

        if not fs.exists(cp_path):
            logger.info("  Aucun checkpoint existant, démarrage à zéro")
            return checkpoint

        statuses = fs.listStatus(cp_path)
        part_files = [s.getPath() for s in statuses if s.getPath().getName().startswith("part-")]

        if not part_files:
            logger.info("  Dossier checkpoint vide")
            return checkpoint

        for part_path in part_files:
            stream = fs.open(part_path)
            reader = spark._jvm.java.io.BufferedReader(
                spark._jvm.java.io.InputStreamReader(stream, "UTF-8")
            )
            line = reader.readLine()
            while line is not None:
                line = line.strip()
                if line:
                    try:
                        record = json.loads(line)
                        house = record["house"]
                        checkpoint[house] = {
                            "last_ts": str(record.get("last_ts", "1970-01-01")),
                            "processed_files": list(record.get("processed_files", [])),
                            "complete": bool(record.get("complete", False))
                        }
                    except:
                        pass
                line = reader.readLine()
            reader.close()

        logger.info(f"   Checkpoint chargé: {len(checkpoint)} maisons")
    except Exception as e:
        logger.error(f"  Erreur chargement checkpoint: {e}")
    return checkpoint

def save_checkpoint(checkpoint):
    try:
        if not checkpoint:
            return

        lines = []
        for house, data in checkpoint.items():
            record = {
                "house": house,
                "last_ts": str(data["last_ts"]),
                "processed_files": [str(f) for f in data["processed_files"]],
                "complete": bool(data["complete"])
            }
            lines.append(json.dumps(record, ensure_ascii=False))
        content = "\n".join(lines) + "\n"

        fs = get_fs()
        Path = spark._jvm.org.apache.hadoop.fs.Path

        temp_path = Path(CHECKPOINT_PATH + "_temp_write")
        final_path = Path(CHECKPOINT_PATH)

        if fs.exists(temp_path):
            fs.delete(temp_path, True)

        part_path = Path(CHECKPOINT_PATH + "_temp_write/part-00000.json")
        out_stream = fs.create(part_path, True)
        writer = spark._jvm.java.io.BufferedWriter(
            spark._jvm.java.io.OutputStreamWriter(out_stream, "UTF-8")
        )
        writer.write(content)
        writer.flush()
        writer.close()

        if fs.exists(final_path):
            fs.delete(final_path, True)

        fs.rename(temp_path, final_path)
    except Exception as e:
        logger.error(f"   Erreur sauvegarde checkpoint: {e}")

# -------------------------------------------------------
# MAIN - Logs simplifiés
# -------------------------------------------------------
logger.info("\n" + "="*60)
logger.info(" BRONZE → SILVER - Traitement incrémental")
logger.info("="*60)

# Chargement checkpoint
logger.info("\n Chargement checkpoint...")
checkpoint = load_checkpoint()

# Lister les maisons
fs = get_fs()
bronze_path = spark._jvm.org.apache.hadoop.fs.Path(BRONZE_PATH)

if not fs.exists(bronze_path):
    logger.error(f" Chemin {BRONZE_PATH} inexistant!")
    spark.stop()
    sys.exit(1)

bronze_status = fs.listStatus(bronze_path)
all_houses = sorted([
    status.getPath().getName()
    for status in bronze_status
    if status.isDirectory() and status.getPath().getName().startswith("house_")
])

houses_to_process = [
    house for house in all_houses
    if house not in checkpoint or not checkpoint[house].get("complete", False)
]

logger.info(f"\n {len(all_houses)} maisons trouvées, {len(houses_to_process)} à traiter")

if not houses_to_process:
    logger.info("\n Toutes les maisons sont déjà à jour !")
    spark.stop()
    sys.exit(0)

# -------------------------------------------------------
# Traitement par maison
# -------------------------------------------------------
total_hours = 0
total_files = 0

for house in houses_to_process:
    logger.info(f"\n{'─'*40}")
    logger.info(f" Traitement {house}")
    logger.info(f"{'─'*40}")

    if house not in checkpoint:
        checkpoint[house] = {
            "last_ts": "1970-01-01",
            "processed_files": [],
            "complete": False
        }
        save_checkpoint(checkpoint)

    house_path = f"{BRONZE_PATH}/{house}"

    # Lire items.tsv
    items_path = f"{house_path}/items.tsv"
    try:
        df_items = spark.read.option("sep", "\t").option("header", "true").csv(items_path)
        df_items = df_items.withColumnRenamed("item_id", "item_id_ref")
        items_count = df_items.count()
        logger.info(f"   {items_count} appareils chargés")
    except Exception as e:
        logger.error(f"   Erreur items.tsv: {e}")
        continue

    # Lister les fichiers
    house_path_java = spark._jvm.org.apache.hadoop.fs.Path(house_path)
    if not fs.exists(house_path_java):
        continue

    data_files = fs.listStatus(house_path_java)
    all_data_paths = [
        status.getPath().toString()
        for status in data_files
        if status.getPath().getName().endswith("data.tsv.gz")
    ]

    processed_files = checkpoint[house]["processed_files"]
    data_paths = [
        path for path in all_data_paths
        if path.split("/")[-1] not in processed_files
    ]

    logger.info(f"   {len(all_data_paths)} fichiers trouvés, {len(data_paths)} nouveaux")

    if not data_paths:
        logger.info(f"   {house} déjà à jour")
        checkpoint[house]["complete"] = True
        save_checkpoint(checkpoint)
        continue

    all_max_ts = checkpoint[house]["last_ts"]
    house_hours = 0

    for data_path in data_paths:
        filename = data_path.split("/")[-1]
        logger.info(f"\n   {filename}")

        try:
            df_raw = spark.read.option("sep", "\t").option("header", "true").csv(data_path)

            df = df_raw \
                .withColumn("timestamp", to_timestamp("time")) \
                .withColumn("value", col("value").cast("double")) \
                .filter(col("timestamp").isNotNull())

            df = df.filter(col("timestamp") > lit(checkpoint[house]["last_ts"]))

            if df.count() == 0:
                logger.info(f"      Aucune nouvelle donnée")
                checkpoint[house]["processed_files"].append(filename)
                save_checkpoint(checkpoint)
                continue

            window = Window.partitionBy("item_id").orderBy("timestamp")

            df = df \
                .withColumn("next_ts", lead("timestamp").over(window)) \
                .withColumn("delta_sec", unix_timestamp("next_ts") - unix_timestamp("timestamp")) \
                .withColumn("consumption_kwh", (col("value") * col("delta_sec")) / 3_600_000)

            df_hour = df \
                .withColumn("timestamp_hour", date_trunc("hour", col("timestamp"))) \
                .groupBy("item_id", "timestamp_hour") \
                .agg(sum("consumption_kwh").alias("consumption_kwh"))

            df_silver = df_hour \
                .join(df_items, df_hour.item_id == df_items.item_id_ref, "left") \
                .withColumn("house_id", lit(house)) \
                .withColumn("co2_kg", col("consumption_kwh") * CO2_FACTOR) \
                .withColumn("year", year("timestamp_hour")) \
                .withColumn("last_modified", current_timestamp()) \
                .select(
                    "house_id",
                    df_hour.item_id,
                    col("label").alias("machine_name"),
                    "timestamp_hour",
                    "consumption_kwh",
                    "co2_kg",
                    "year",
                    "last_modified"
                )

            count = df_silver.count()
            if count > 0:
                output_path = f"{SILVER_PATH}/{house}"
                df_silver.write.mode("append").partitionBy("year").parquet(output_path)
                logger.info(f"     {count} heures enregistrées")
                house_hours += count
                total_hours += count
            else:
                logger.info(f"     aucune donnée après agrégation")

            file_max_ts = df.select(max("timestamp")).collect()[0][0]
            if file_max_ts and str(file_max_ts) > all_max_ts:
                all_max_ts = str(file_max_ts)

            checkpoint[house]["processed_files"].append(filename)
            checkpoint[house]["last_ts"] = all_max_ts
            save_checkpoint(checkpoint)
            total_files += 1

        except Exception as e:
            logger.error(f"     Erreur: {str(e)}")
            continue

    if house_hours > 0:
        processed_set = set(checkpoint[house]["processed_files"])
        all_files_set = set([p.split("/")[-1] for p in all_data_paths])

        if processed_set >= all_files_set:
            checkpoint[house]["complete"] = True
            logger.info(f"\n   {house}: {house_hours} heures ajoutées")
        else:
            remaining = all_files_set - processed_set
            logger.info(f"\n   {house}: {house_hours} heures, reste {len(remaining)} fichiers")

    save_checkpoint(checkpoint)

# Résumé final
logger.info("\n" + "="*60)
logger.info(f" TRAITEMENT TERMINÉ")
logger.info(f"   • {total_files} nouveaux fichiers traités")
logger.info(f"   • {total_hours} heures ajoutées en Silver")
logger.info("="*60)

spark.stop()