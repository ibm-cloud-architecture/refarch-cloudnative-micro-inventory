#!/bin/bash

function parse_arguments() {
	# MICROSERVICE_HOST
	if [ -z "${MICROSERVICE_HOST}" ]; then
		echo "MICROSERVICE_HOST not set. Using parameter \"$1\"";
		MICROSERVICE_HOST=$1;
	fi

	if [ -z "${MICROSERVICE_HOST}" ]; then
		echo "MICROSERVICE_HOST not set. Using default key";
		MICROSERVICE_HOST=127.0.0.1;
	fi

	# MICROSERVICE_PORT
	if [ -z "${MICROSERVICE_PORT}" ]; then
		echo "MICROSERVICE_PORT not set. Using parameter \"$2\"";
		MICROSERVICE_PORT=$2;
	fi

	if [ -z "${MICROSERVICE_PORT}" ]; then
		echo "MICROSERVICE_PORT not set. Using default key";
		MICROSERVICE_PORT=8080;
	fi

	echo "Using http://${MICROSERVICE_HOST}:${MICROSERVICE_PORT}"
}

function get_inventory() {
	CURL=$(curl -s --max-time 5 http://${MICROSERVICE_HOST}:${MICROSERVICE_PORT}/micro/inventory | jq '. | length');
	echo "Found inventory with \"${CURL}\" items"

	if [ -z "${CURL}" ] || [ ! "${CURL}" -gt "0" ]; then
		echo "get_inventory: ❌ could not get inventory";
        exit 1;
    else
    	echo "get_inventory: ✅";
    fi
}

# Setup
parse_arguments $1 $2

# API Tests
echo "Starting Tests"
get_inventory