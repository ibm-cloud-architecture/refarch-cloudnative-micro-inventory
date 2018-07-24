#!/bin/bash
source scripts/max_heap.sh
source scripts/parse_mysql.sh

# Set Max Heap
export JAVA_OPTS="${JAVA_OPTS} -Xmx${max_heap}m"

# Set basic java options
export JAVA_OPTS="${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom"

#set -x
# Parse Elasticsearch info and put it into JAVA_OPTS
parse_mysql

# Checks for messagehub "variable" set by Kubernetes secret
if [ -z ${messagehub+x} ]; then 
    echo "Secret not in \"messagehub\" variable. Probably NOT running in Kubernetes";
else 
    echo "Found messagehub secret"

    # Construct messagehub environment variables
    mh_user=$(echo $messagehub  | jq -r '.user')
    mh_password=$(echo $messagehub  | jq -r '.password')
    mh_api_key=$(echo $messagehub  | jq -r '.api_key')
    mh_kafka_rest_url=$(echo $messagehub  | jq -r '.kafka_rest_url')
    mh_kafka_server_0=$(echo $messagehub  | jq -r '.kafka_brokers_sasl[0]')
    mh_kafka_server_1=$(echo $messagehub  | jq -r '.kafka_brokers_sasl[1]')
    mh_kafka_server_2=$(echo $messagehub  | jq -r '.kafka_brokers_sasl[2]')
    mh_kafka_server_3=$(echo $messagehub  | jq -r '.kafka_brokers_sasl[3]')
    mh_kafka_server_4=$(echo $messagehub  | jq -r '.kafka_brokers_sasl[4]')

    JAVA_OPTS="${JAVA_OPTS} -Dmessage_hub.user=${mh_user}"
    JAVA_OPTS="${JAVA_OPTS} -Dmessage_hub.password=${mh_password}"
    JAVA_OPTS="${JAVA_OPTS} -Dmessage_hub.api_key=${mh_api_key}"
    JAVA_OPTS="${JAVA_OPTS} -Dmessage_hub.kafka_rest_url=${mh_kafka_rest_url}"
    JAVA_OPTS="${JAVA_OPTS} -Dmessage_hub.kafka_brokers_sasl[0]=${mh_kafka_server_0}"
    JAVA_OPTS="${JAVA_OPTS} -Dmessage_hub.kafka_brokers_sasl[1]=${mh_kafka_server_1}"
    JAVA_OPTS="${JAVA_OPTS} -Dmessage_hub.kafka_brokers_sasl[2]=${mh_kafka_server_2}"
    JAVA_OPTS="${JAVA_OPTS} -Dmessage_hub.kafka_brokers_sasl[3]=${mh_kafka_server_3}"
    JAVA_OPTS="${JAVA_OPTS} -Dmessage_hub.kafka_brokers_sasl[4]=${mh_kafka_server_4}"
fi

# disable eureka
JAVA_OPTS="${JAVA_OPTS} -Deureka.client.enabled=false -Deureka.client.registerWithEureka=false -Deureka.fetchRegistry=false"

echo "Starting Java Application"

set +x
# Start the application
exec java ${JAVA_OPTS} -jar /app.jar