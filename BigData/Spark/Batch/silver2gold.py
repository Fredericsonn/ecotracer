#!/usr/bin/env python3
from pyspark.sql import SparkSession
from pyspark.sql.functions import *
import pyspark.sql.functions as func
import psycopg2
from psycopg2.extras import execute_batch
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
    .appName("Energy_Silver_To_Gold") \
    .master("local[4]") \
    .config("spark.sql.adaptive.enabled", "true") \
    .config("spark.sql.shuffle.partitions", "8") \
    .config("spark.ui.showConsoleProgress", "false") \
    .config("spark.logConf", "false") \
    .getOrCreate()

spark.sparkContext.setLogLevel("ERROR")

SILVER_PATH = "hdfs://172.31.253.133:9000/silver/energy"
CHECKPOINT_PATH = "hdfs://172.31.253.133:9000/gold/checkpoints/energy_gold_checkpoint.json"

POSTGRES_CONFIG = {
    "host": "172.31.253.125",
    "port": 5432,
    "database": "energy_db",
    "user": "eco",
    "password": "energy2025"
}

logger.info("\n" + "="*60)
logger.info(" SILVER → GOLD - Agrégation quotidienne")
logger.info("="*60)

# Détecter les maisons
fs = spark._jvm.org.apache.hadoop.fs.FileSystem.get(spark._jsc.hadoopConfiguration())
silver_path = spark._jvm.org.apache.hadoop.fs.Path(SILVER_PATH)

if not fs.exists(silver_path):
    logger.error(f" Le chemin {SILVER_PATH} n'existe pas!")
    spark.stop()
    sys.exit(1)

silver_status = fs.listStatus(silver_path)
houses = sorted([
    status.getPath().getName()
    for status in silver_status
    if status.isDirectory() and status.getPath().getName().startswith("house_")
])
logger.info(f"\n {len(houses)} maisons détectées en Silver")

# Charger checkpoint Gold
try:
    checkpoint_df = spark.read.json(f"{CHECKPOINT_PATH}/*.json")
    last_checkpoint = checkpoint_df.collect()[0]["last_modified"]
    logger.info(f" Checkpoint trouvé : {last_checkpoint}")
except Exception as e:
    last_checkpoint = "1970-01-01 00:00:00"
    logger.info(" Premier run Gold (1970-01-01)")

# Lire Silver incrémental
logger.info("\n Lecture des nouvelles données...")
df_list = []

for house in houses:
    house_path = f"{SILVER_PATH}/{house}"
    df = spark.read.parquet(house_path)

    if "last_modified" in df.columns:
        df = df.filter(col("last_modified") > lit(last_checkpoint))
        count = df.count()
        if count > 0:
            logger.info(f"   {house}: {count} nouvelles lignes")
            df_list.append(df)
    else:
        logger.info(f"  {house} : pas de last_modified, ignoré")

if not df_list:
    logger.info("\n Aucune donnée nouvelle")
    spark.stop()
    sys.exit(0)

df_silver = df_list[0]
for df in df_list[1:]:
    df_silver = df_silver.union(df)

logger.info(f"\n Total nouvelles lignes: {df_silver.count()}")

# Transformation GOLD
df_gold = df_silver \
    .withColumn("date", to_date("timestamp_hour")) \
    .groupBy("house_id", "machine_name", "date") \
    .agg(
        sum("consumption_kwh").alias("daily_kwh"),
        sum("co2_kg").alias("daily_co2_kg"),
        func.count(lit(1)).alias("hours_active")
    ) \
    .select(
        "house_id",
        "machine_name",
        "date",
        "daily_kwh",
        "daily_co2_kg",
        "hours_active"
    )

logger.info("\n Échantillon données Gold:")
df_gold.show(5, truncate=False)

# Convertir en Pandas pour PostgreSQL
pdf = df_gold.toPandas()
logger.info(f"\n {len(pdf)} lignes à upsert dans PostgreSQL")

# UPSERT dans PostgreSQL
conn = psycopg2.connect(**POSTGRES_CONFIG)
cursor = conn.cursor()

# Vérifier que la table existe
cursor.execute("""
    SELECT EXISTS (
        SELECT FROM information_schema.tables
        WHERE table_name = 'energy_gold'
    );
""")
table_exists = cursor.fetchone()[0]

if not table_exists:
    logger.info("    Création de la table energy_gold...")
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS energy_gold (
            house_id VARCHAR(20),
            machine_name VARCHAR(100),
            date DATE,
            daily_kwh DECIMAL(10,3),
            daily_co2_kg DECIMAL(10,3),
            hours_active INTEGER,
            last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            PRIMARY KEY (house_id, machine_name, date)
        );
    """)
    conn.commit()
    logger.info("    Table energy_gold créée")

# UPSERT query
upsert_query = """
INSERT INTO energy_gold (house_id, machine_name, date, daily_kwh, daily_co2_kg, hours_active)
VALUES (%s, %s, %s, %s, %s, %s)
ON CONFLICT (house_id, machine_name, date)
DO UPDATE SET
    daily_kwh = EXCLUDED.daily_kwh,
    daily_co2_kg = EXCLUDED.daily_co2_kg,
    hours_active = EXCLUDED.hours_active,
    last_updated = CURRENT_TIMESTAMP;
"""

execute_batch(cursor, upsert_query, pdf.values.tolist())
conn.commit()

logger.info(f"    {len(pdf)} lignes upserted dans energy_gold")

max_last_modified = df_silver.agg(max("last_modified")).collect()[0][0]

if max_last_modified is not None:
    checkpoint_df = spark.createDataFrame(
        [(str(max_last_modified),)],  
        ["last_modified"]
    )

    # Écrire checkpoint 
    temp_path = CHECKPOINT_PATH + "_temp"

    # Supprimer l'ancien temp
    if fs.exists(spark._jvm.org.apache.hadoop.fs.Path(temp_path)):
        fs.delete(spark._jvm.org.apache.hadoop.fs.Path(temp_path), True)

    checkpoint_df.coalesce(1).write.mode("overwrite").json(temp_path)

    # Supprimer l'ancien checkpoint
    path = spark._jvm.org.apache.hadoop.fs.Path(CHECKPOINT_PATH)
    if fs.exists(path):
        fs.delete(path, True)

    # Renommer temp vers checkpoint
    fs.rename(spark._jvm.org.apache.hadoop.fs.Path(temp_path), path)

    logger.info(f"\n Checkpoint mis à jour : {str(max_last_modified)}")

logger.info("\n" + "="*60)
logger.info(" TRAITEMENT SILVER → GOLD TERMINÉ")
logger.info("="*60)

spark.stop()