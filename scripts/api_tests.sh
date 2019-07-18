#!/bin/bash

function parse_arguments() {
	#set -x;
	# INVENTORY_HOST
	if [ -z "${INVENTORY_HOST}" ]; then
		echo "INVENTORY_HOST not set. Using parameter \"$1\"";
		INVENTORY_HOST=$1;
	fi

	if [ -z "${INVENTORY_HOST}" ]; then
		echo "INVENTORY_HOST not set. Using default key";
		INVENTORY_HOST=127.0.0.1;
	fi

	# INVENTORY_PORT
	if [ -z "${INVENTORY_PORT}" ]; then
		echo "INVENTORY_PORT not set. Using parameter \"$2\"";
		INVENTORY_PORT=$2;
	fi

	if [ -z "${INVENTORY_PORT}" ]; then
		echo "INVENTORY_PORT not set. Using default key";
		INVENTORY_PORT=9080;
	fi

	#set +x;
}

function get_inventory() {
	CURL=$(curl -s --max-time 5 http://${INVENTORY_HOST}:${INVENTORY_PORT}/inventory/rest/inventory | jq '. | length');
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
