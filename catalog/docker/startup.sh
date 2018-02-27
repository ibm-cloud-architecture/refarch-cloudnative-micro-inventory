#!/bin/bash

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
if [ ! -z ${elastic+x} ]; then 
    el_uri=$(echo $elastic | jq -r .uri)

    JAVA_OPTS="${JAVA_OPTS} -Dspring.data.jest.uri=${el_url}"

    cert=$(echo $elastic | jq .ca_certificate_base64 -r);
    if [[ ! -z "${cert// }" && "$cert" != "null" ]]; then
        echo "Updating bluecompute-ca-certificate.crt"
        echo $elastic | jq .ca_certificate_base64 -r | base64 -d >> /etc/ssl/certs/bluecompute-ca-certificate.crt

        echo "Updating Java keystore"
        ls -l $JAVA_HOME/jre/lib/security/cacerts

        keytool -import -noprompt -trustcacerts -alias bluecompute -file /etc/ssl/certs/bluecompute-ca-certificate.crt -keystore $JAVA_HOME/jre/lib/security/cacerts -storepass changeit
        keytool -list -keystore $JAVA_HOME/jre/lib/security/cacerts -storepass changeit | grep bluecompute
    else
        echo "No certificate to update"
    fi
fi

# Load agent support if required
#source ./agents/newrelic.sh

echo "Starting Java application"

# Start the application
exec java ${JAVA_OPTS} -jar /app.jar
