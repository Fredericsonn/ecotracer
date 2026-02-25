from airflow import DAG
from airflow.operators.bash import BashOperator
from datetime import datetime, timedelta

default_args = {
    'owner': 'eco',
    'retries': 1,
    'retry_delay': timedelta(minutes=5),
}


SPARK_SUBMIT = (
    "docker exec spark-master /opt/spark/bin/spark-submit "
    "--master spark://172.31.252.37:7077 "  
    "--driver-memory 8g "
    "--executor-memory 2g "                  
    "--executor-cores 2 "                     
    "--conf spark.sql.shuffle.partitions=200 "
    "--conf spark.sql.adaptive.enabled=true "
    "--conf spark.sql.adaptive.coalescePartitions.enabled=true "
    "--conf spark.serializer=org.apache.spark.serializer.KryoSerializer "
    "--jars /opt/spark/jars/postgresql-42.7.1.jar "
)

with DAG(
    dag_id='energy_batch_pipeline',
    default_args=default_args,
    description='ETL batch : Bronze -> Silver -> Gold ',
    schedule_interval='0 3 * * *',
    start_date=datetime(2025, 1, 1),
    catchup=False,
    tags=['energy', 'batch', 'spark'],
) as dag:

    bronze_to_silver = BashOperator(
        task_id='bronze_to_silver',
        bash_command=(
            "docker exec spark-master mkdir -p /opt/spark/apps/batch && "
            "docker cp /home/eco/spark-cluster/apps/batch/energy_silver_fixed.py spark-master:/opt/spark/apps/batch/ && "
            + SPARK_SUBMIT + "/opt/spark/apps/batch/energy_silver_fixed.py"
        ),
    )

    silver_to_gold = BashOperator(
        task_id='silver_to_gold',
        bash_command=(
            "docker exec spark-master mkdir -p /opt/spark/apps/batch && "
            "docker cp /home/eco/spark-cluster/apps/batch/energy_silver2gold_fixed.py spark-master:/opt/spark/apps/batch/ && "
            + SPARK_SUBMIT + "/opt/spark/apps/batch/energy_silver2gold_fixed.py"
        ),
    )

    bronze_to_silver >> silver_to_gold