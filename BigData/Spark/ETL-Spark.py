from pyspark.sql import SparkSession
from pyspark.sql.functions import *
from pyspark.sql.types import *

# Configuration
NAMENODE_IP = "172.31.251.96"  # IP du NameNode
HDFS_INPUT = f"hdfs://{NAMENODE_IP}:9000/data/deddiag/raw/house_00/item_0010_data.csv"
HDFS_OUTPUT = f"hdfs://{NAMENODE_IP}:9000/data/deddiag/curated/house_00_hourly"
EMISSION_FACTOR = 0.485  # kgCO2/kWh Allemagne (moyenne)

# Spark Session
spark = SparkSession.builder \
    .appName("DEDDIAG_CO2_Processing") \
    .config("spark.hadoop.fs.defaultFS", f"hdfs://{NAMENODE_IP}:9000") \
    .getOrCreate()

# Schéma
schema = StructType([
    StructField("item_id", IntegerType(), True),
    StructField("time", StringType(), True),
    StructField("value", DoubleType(), True)
])

# Lecture
df = spark.read.csv(HDFS_INPUT, header=True, schema=schema)

# Conversion timestamp + agrégation horaire
df_hourly = df \
    .withColumn("timestamp", to_timestamp("time")) \
    .withColumn("hour", date_trunc("hour", "timestamp")) \
    .groupBy("item_id", "hour") \
    .agg(
        avg("value").alias("avg_power_watts"),
        count("*").alias("num_measurements")
    ) \
    .withColumn("energy_kwh", col("avg_power_watts") * col("num_measurements") / 3600 / 1000) \
    .withColumn("emission_factor", lit(EMISSION_FACTOR)) \
    .withColumn("co2_kg", col("energy_kwh") * col("emission_factor"))

# Sauvegarde Parquet
df_hourly.write.mode("overwrite").parquet(HDFS_OUTPUT)

print(f"✅ Processing complete. Output: {HDFS_OUTPUT}")
spark.stop()