#!/bin/bash
# URI parsing function
#
# The function creates global variables with the parsed results.
# It returns 0 if parsing was successful or non-zero otherwise.
#
# [schema://][user[:password]@]host[:port][/path][?[arg1=val1]...][#fragment]
#
# from http://vpalos.com/537/uri-parsing-using-bash-built-in-features/
#
function uri_parser() {
    # uri capture
    uri="$@"

    # safe escaping
    uri="${uri//\`/%60}"
    uri="${uri//\"/%22}"

    # top level parsing
    pattern='^(([a-z]{3,5})://)?((([^:\/]+)(:([^@\/]*))?@)?([^:\/?]+)(:([0-9]+))?)(\/[^?]*)?(\?[^#]*)?(#.*)?$'
    [[ "$uri" =~ $pattern ]] || return 1;

    # component extraction
    uri=${BASH_REMATCH[0]}
    uri_schema=${BASH_REMATCH[2]}
    uri_address=${BASH_REMATCH[3]}
    uri_user=${BASH_REMATCH[5]}
    uri_password=${BASH_REMATCH[7]}
    uri_host=${BASH_REMATCH[8]}
    uri_port=${BASH_REMATCH[10]}
    uri_path=${BASH_REMATCH[11]}
    uri_query=${BASH_REMATCH[12]}
    uri_fragment=${BASH_REMATCH[13]}

    # path parsing
    count=0
    path="$uri_path"
    pattern='^/+([^/]+)'
    while [[ $path =~ $pattern ]]; do
        eval "uri_parts[$count]=\"${BASH_REMATCH[1]}\""
        path="${path:${#BASH_REMATCH[0]}}"
        let count++
    done

    # query parsing
    count=0
    query="$uri_query"
    pattern='^[?&]+([^= ]+)(=([^&]*))?'
    while [[ $query =~ $pattern ]]; do
        eval "uri_args[$count]=\"${BASH_REMATCH[1]}\""
        eval "uri_arg_${BASH_REMATCH[1]}=\"${BASH_REMATCH[3]}\""
        query="${query:${#BASH_REMATCH[0]}}"
        let count++
    done

    # return success
    return 0
}

# Set basic java options
export JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom"

# Checks for elastic "variable" set by Kubernetes secret
if [ -z ${elastic+x} ]; then 
	echo "Secret not in \"elastic\" variable. Probably NOT running in Kubernetes";
else 
	echo "Found elastic secret"
    el_uri=$(echo $elastic | jq .uri | sed s%\"%%g)

	# Do the URL parsing
	uri_parser $el_uri

	# Construct elasticsearch url
	el_url="${uri_schema}://${uri_host}:${uri_port}"
	el_user=${uri_user}
	el_password=${uri_password}

    JAVA_OPTS="${JAVA_OPTS} -Delasticsearch.url=${el_url}"
    JAVA_OPTS="${JAVA_OPTS} -Delasticsearch.user=${el_user}"
    JAVA_OPTS="${JAVA_OPTS} -Delasticsearch.password=${el_password}"
fi

# Checks for mysql "variable" set by Kubernetes secret
if [ -z ${mysql+x} ]; then 
    echo "Secret not in \"mysql\" variable. Probably NOT running in Kubernetes";
else 
    echo "Found mysql secret"
    mysql_uri=$(echo $mysql | jq .uri | sed s%\"%%g)

    # Do the URL parsing
    uri_parser $mysql_uri

    # Construct elasticsearch url
    mysql_url="jdbc:${uri_schema}://${uri_host}:${uri_port}/inventorydb"
    mysql_user=${uri_user}
    mysql_password=${uri_password}
    mysql_port=${uri_port}

    JAVA_OPTS="${JAVA_OPTS} -Dspring.datasource.url=${mysql_url}"
    JAVA_OPTS="${JAVA_OPTS} -Dspring.datasource.username=${mysql_user}"
    JAVA_OPTS="${JAVA_OPTS} -Dspring.datasource.password=${mysql_password}"
    JAVA_OPTS="${JAVA_OPTS} -Dspring.datasource.port=${mysql_port}"

fi

# Checks for messagehub "variable" set by Kubernetes secret
if [ -z ${messagehub+x} ]; then 
    echo "Secret not in \"messagehub\" variable. Probably NOT running in Kubernetes";
else 
    echo "Found messagehub secret"

    # Construct messagehub environment variables
    mh_user=$(echo $messagehub | jq .user | sed s%\"%%g)
    mh_password=$(echo $messagehub | jq .password | sed s%\"%%g)
    mh_api_key=$(echo $messagehub | jq .api_key | sed s%\"%%g)
    mh_kafka_rest_url=$(echo $messagehub | jq .kafka_rest_url | sed s%\"%%g)
    mh_kafka_server_0=$(echo $messagehub | jq .kafka_brokers_sasl[0] | sed s%\"%%g | sed s%\'%%g)
    mh_kafka_server_1=$(echo $messagehub | jq .kafka_brokers_sasl[1] | sed s%\"%%g | sed s%\'%%g)
    mh_kafka_server_2=$(echo $messagehub | jq .kafka_brokers_sasl[2] | sed s%\"%%g | sed s%\'%%g)
    mh_kafka_server_3=$(echo $messagehub | jq .kafka_brokers_sasl[3] | sed s%\"%%g | sed s%\'%%g)
    mh_kafka_server_4=$(echo $messagehub | jq .kafka_brokers_sasl[4] | sed s%\"%%g | sed s%\'%%g)

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

# Load agent support if required
source ./agents/newrelic.sh

echo "Starting Java Application"

# Start the application
exec java ${JAVA_OPTS} -jar /app.jar