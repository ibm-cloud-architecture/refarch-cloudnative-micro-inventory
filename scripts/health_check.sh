#!/bin/bash
URL="$1";
HEALTH_CHECK="health";

if [ -z "$URL" ]; then
	URL="http://localhost:8090"
	echo "No URL provided! Using ${URL}"
fi

HEALTH_URL="${URL}/${HEALTH_CHECK}"

# Load Generation
echo "Health Check on \"${HEALTH_URL}\"";

function is_healthy {
	curl -s ${HEALTH_URL} | grep "status" | grep "UP";
}

HEALTHY=$(is_healthy)

echo -n "Waiting for service to be ready"

until [ -n "$HEALTHY" ] && [ "${HEALTHY}" == '{"status":"UP"}' ]; do
	HEALTHY=$(is_healthy);
	echo -n .;
	sleep 1;
done

printf "\nService is ready\n"