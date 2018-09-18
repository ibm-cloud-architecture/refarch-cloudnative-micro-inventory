#!/bin/bash

function parse_arguments() {
	#set -x;
	# INVENTORYHOST
	if [ -z "${INVENTORYHOST}" ]; then
		echo "INVENTORYHOST not set. Using parameter \"$1\"";
		INVENTORYHOST=$1;
	fi

	if [ -z "${INVENTORYHOST}" ]; then
		echo "INVENTORYHOST not set. Using default key";
		INVENTORYHOST=127.0.0.1;
	fi

	# INVENTORYPORT
	if [ -z "${INVENTORYPORT}" ]; then
		echo "INVENTORYPORT not set. Using parameter \"$2\"";
		INVENTORYPORT=$2;
	fi

	if [ -z "${INVENTORYPORT}" ]; then
		echo "INVENTORYPORT not set. Using default key";
		INVENTORYPORT=8080;
	fi

	#set +x;
}

function get_inventory() {
	CURL=$(curl -s --max-time 5 http://${INVENTORYHOST}:${INVENTORYPORT}/micro/inventory | jq '. | length');
	#echo "Found inventory with \"${CURL}\" items"

	if [ ! "$CURL" -gt "0" ]; then
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