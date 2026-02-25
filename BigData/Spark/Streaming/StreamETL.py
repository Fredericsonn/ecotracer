#!/usr/bin/env python3
from pyspark.sql import SparkSession
from pyspark.sql.functions import *
from pyspark.sql.types import *

# Configuration
KAFKA_BROKER = "172.31.249.119:9092"
KAFKA_TOPIC = "energy-streaming"
HDFS_PATH = "hdfs://172.31.253.133:9000/streaming"
POSTGRES_URL = "jdbc:postgresql://172.31.253.125:5432/energy_db"
POSTGRES_USER = "eco"
POSTGRES_PASSWORD = "energy2025"

# Créer SparkSession
spark = SparkSession.builder \
    .appName("Kafka_Streaming_To_HDFS_Postgres") \
    .config("spark.jars", "/opt/spark/jars/spark-sql-kafka-0-10_2.12-3.5.0.jar,/opt/spark/jars/postgresql-42.7.1.jar") \
    .config("spark.sql.streaming.schemaInference", "true") \
    .config("spark.sql.adaptive.enabled", "true") \
    .getOrCreate()

# Réduire le niveau de log Spark
spark.sparkContext.setLogLevel("WARN")

# Schéma des données JSON
schema = StructType([
    StructField("house_id", StringType()),
    StructField("item_id", StringType()),
    StructField("machine_name", StringType()),
    StructField("date", StringType()),
    StructField("time", StringType()),
    StructField("power_watt", DoubleType())
])

# Lire depuis Kafka
df_kafka = spark \
    .readStream \
    .format("kafka") \
    .option("kafka.bootstrap.servers", KAFKA_BROKER) \
    .option("subscribe", KAFKA_TOPIC) \
    .option("startingOffsets", "latest") \
    .option("failOnDataLoss", "false") \
    .load()

# Parser le JSON
df_parsed = df_kafka \
    .select(from_json(col("value").cast("string"), schema).alias("data")) \
    .select("data.*")

# Fonction pour écrire dans PostgreSQL avec logs simplifiés
def write_to_postgres(batch_df, batch_id):
    count = batch_df.count()
    if count > 0:
        batch_df.write \
            .format("jdbc") \
            .option("url", POSTGRES_URL) \
            .option("dbtable", "energy_streaming") \
            .option("user", POSTGRES_USER) \
            .option("password", POSTGRES_PASSWORD) \
            .option("driver", "org.postgresql.Driver") \
            .mode("append") \
            .save()
        print(f" Batch {batch_id}: {count} lignes écrites dans PostgreSQL")
    else:
        print(f"Batch {batch_id}: 0 lignes (en attente)")

# Stream vers PostgreSQL
query_postgres = df_parsed \
    .writeStream \
    .foreachBatch(write_to_postgres) \
    .outputMode("append") \
    .trigger(processingTime="10 seconds") \
    .start()

# Stream vers HDFS (Parquet) avec logs silencieux
query_hdfs = df_parsed \
    .writeStream \
    .format("parquet") \
    .option("path", HDFS_PATH) \
    .option("checkpointLocation", f"{HDFS_PATH}/checkpoint") \
    .outputMode("append") \
    .trigger(processingTime="10 seconds") \
    .start()

print("\n" + "="*50)
print("SPARK STREAMING ACTIF")
print("="*50)
print("Kafka → HDFS + PostgreSQL")
print(f"criture toutes les 10 secondes")
print(f"Topics: {KAFKA_TOPIC}")
print(f"ostgreSQL: {POSTGRES_URL}")
print("="*50 + "\n")

# Attendre la terminaison
query_postgres.awaitTermination()
query_hdfs.awaitTermination()