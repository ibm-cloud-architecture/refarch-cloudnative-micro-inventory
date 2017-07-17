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

# find the java heap size as 50% of container memory using sysfs
# find the java heap size as 50% of container memory using sysfs, or 512m whichever is less
max_heap=`echo "512 * 1024 * 1024" | bc`
if [ -r "/sys/fs/cgroup/memory/memory.limit_in_bytes" ]; then
    mem_limit=`cat /sys/fs/cgroup/memory/memory.limit_in_bytes`
    if [ ${mem_limit} -lt ${max_heap} ]; then
        max_heap=${mem_limit}
    fi
fi
max_heap=`echo "(${max_heap} / 1024 / 1024) / 2" | bc`
export JAVA_OPTS="${JAVA_OPTS} -Xmx${max_heap}m"

# Set basic java options
export JAVA_OPTS="${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom"

# Checks for elastic "variable" set by Kubernetes secret
if [ -z ${elastic+x} ]; then 
    echo "Secret not in \"elastic\" variable. Probably NOT running in Kubernetes";
else 
	echo "Running in Kubernetes";
    el_uri=$(echo $elastic | base64 -d | jq -r .uri)

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

# disable eureka
JAVA_OPTS="${JAVA_OPTS} -Deureka.client.enabled=false -Deureka.client.registerWithEureka=false -Deureka.fetchRegistry=false"

# Load agent support if required
source ./agents/newrelic.sh

echo "Starting Java application"

# Start the application
exec java ${JAVA_OPTS} -jar /app.jar
