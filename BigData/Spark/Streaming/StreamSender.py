import kagglehub
import pandas as pd
import paho.mqtt.client as mqtt
import json
import time
from datetime import datetime
import os

# Configuration
MQTT_BROKER = "172.31.252.147"
MQTT_PORT = 1883
MQTT_TOPIC = "energy/house_01/sensor_data"
HOUSE_ID = "house_01"

# Télécharger le dataset depuis Kaggle
print("Téléchargement du dataset depuis Kaggle...")
dataset_path = kagglehub.dataset_download("khalilaraoui/power-telemetry-deddiag")
print(f"Dataset téléchargé dans: {dataset_path}")

# Chemins des fichiers
house_path = os.path.join(dataset_path, HOUSE_ID)
items_file = os.path.join(house_path, "items.tsv")

# Charger le mapping des machines
print("Chargement des informations machines...")
df_items = pd.read_csv(items_file, sep='\t')
items_map = dict(zip(df_items['item_id'].astype(str), df_items['label']))
print(f" {len(items_map)} machines trouvées: {list(items_map.values())}")

# Lister tous les fichiers data (NON compressés)
data_files = [f for f in os.listdir(house_path) if f.endswith('_data.tsv')]
print(f" {len(data_files)} fichiers de données trouvés")

# Connexion MQTT
print(f" Connexion à MQTT broker {MQTT_BROKER}:{MQTT_PORT}...")
client = mqtt.Client()
client.connect(MQTT_BROKER, MQTT_PORT, 60)
client.loop_start()
print(" Connecté à MQTT")

# Traitement et envoi des données
total_sent = 0
for data_file in sorted(data_files):
    print(f"\n Traitement: {data_file}")
    file_path = os.path.join(house_path, data_file)

    # Lire le fichier TSV
    df = pd.read_csv(file_path, sep='\t')
    print(f"    {len(df)} lignes à envoyer")

    # Traiter ligne par ligne
    for idx, row in df.iterrows():
        # Parser le timestamp
        ts = pd.to_datetime(row['time'])

        # Préparer le message
        message = {
            "house_id": HOUSE_ID,
            "item_id": str(row['item_id']),
            "machine_name": items_map.get(str(row['item_id']), "Unknown"),
            "date": ts.strftime("%Y/%m/%d"),
            "time": ts.strftime("%H:%M:%S:%f")[:-3],  # milliseconds
            "power_watt": float(row['value'])
        }

        # Envoyer à MQTT
        client.publish(MQTT_TOPIC, json.dumps(message))
        total_sent += 1

        if total_sent % 100 == 0:
            print(f" {total_sent} messages envoyés...")

        # Attendre 1 seconde (simulation capteur)
        time.sleep(1)

print(f"\n Terminé ! {total_sent} messages envoyés au total")
client.loop_stop()
client.disconnect()