#!/usr/bin/env python3
import paho.mqtt.client as mqtt
from kafka import KafkaProducer
import json

# Configuration
MQTT_BROKER = "172.31.252.147"
MQTT_PORT = 1883
MQTT_TOPIC = "energy/house_01/sensor_data"

KAFKA_BROKER = "172.31.249.119:9092"
KAFKA_TOPIC = "energy-streaming"

# Kafka Producer
producer = KafkaProducer(
    bootstrap_servers=KAFKA_BROKER,
    value_serializer=lambda v: json.dumps(v).encode('utf-8')
)

# MQTT Callbacks
def on_connect(client, userdata, flags, rc):
    print(f" Connecté à MQTT (code: {rc})")
    client.subscribe(MQTT_TOPIC)
    print(f" Souscrit au topic: {MQTT_TOPIC}")

def on_message(client, userdata, msg):
    try:
        # Décoder le message MQTT
        data = json.loads(msg.payload.decode('utf-8'))

        # Envoyer vers Kafka
        producer.send(KAFKA_TOPIC, value=data)
        producer.flush()

        print(f" Envoyé vers Kafka: {data['machine_name']} - {data['power_watt']}W")
    except Exception as e:
        print(f" Erreur: {e}")

# Client MQTT
client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message

print(f" Connexion à MQTT {MQTT_BROKER}:{MQTT_PORT}...")
client.connect(MQTT_BROKER, MQTT_PORT, 60)

print(f" Connexion à Kafka {KAFKA_BROKER}...")
print(" Bridge MQTT → Kafka actif\n")

client.loop_forever()