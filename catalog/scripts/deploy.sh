#!/bin/bash

# GLOBAL VARIABLES
# Constants
pipeline_name="catalog"
build_number=$1
image_name="registry.ng.bluemix.net/chrisking/${pipeline_name}:${build_number}"
token=$(cat /var/run/secrets/kubernetes.io/serviceaccount/token)
cluster_name=$(cat /var/run/secrets/bx-auth-secret/CLUSTER_NAME)
chart_repo=$(cat /var/run/configs/github/chart.repo)
values_file="values.yaml"
bx_offering_name_elasticsearch="compose-for-elasticsearch"

# To be filled and used for new deployment
bx_service_instance_elasticsearch=""

kube_secret_elasticsearch=""

function get_kube_secret {
	echo $(kubectl --token=${token} get secrets | grep "$1" | awk '{print $1}')
}

function get_kube_service_name {
	echo $(kubectl --token=${token} get services | grep "$1" | awk '{print $1}')
}

function get_kube_service_info {
	echo $(kubectl --token=${token} get services $1 | grep $1 | head -1)
}

function get_instance_for_bluemix_service {
	local service_name=$1

	echo "Getting service instance for ${service_name}"
	local service=$(bx service list | grep "${service_name}" | head -1 | awk '{print $1}')
	
	# Check if queried a valid service
	echo "Checking if ${service} is valid"
	bx service show "$service"

	local status=$?
    if [ $status -ne 0 ]; then
    	# Checking if instance name has spaces in it's name
		echo "Trying again... Getting service instance for ${service_name}"
    	service=$(bx service list | grep "${service_name}" | head -1 | sed -e 's/${service_name}.*//' | sed 's/[[:blank:]]*$//')

    	# Check if queried a valid service
		echo "Checking if ${service} is valid"
		bx service show "$service"
		status=$?
	    if [ $status -ne 0 ]; then
	    	echo "Could not find valid service instance for ${}... Exiting"
	    	exit 1
	    fi
    fi

    # Set global variables
	if [ "$service_name" == "$bx_offering_name_elasticsearch" ]; then
		echo "Found service instance for elasticsearch"
	    bx_service_instance_elasticsearch=$service
	fi
}

function bind_bx_service_instance_to_kubernetes {
	local instance=$1
	# Binding the service
	# If already bound, it should still succeed
	echo "Binding service instance ${instance}"
	bx cs cluster-service-bind $cluster_name default $instance

	local status=$?

	if [ $status -ne 0 ]; then
		echo "Something went wrong binding ${instance}... Exiting"
		exit 1
	fi
}

function put_secret_in_values {
	local secret=$1
	local index=$2
	local string_to_replace=$(yaml read ${values_file} secrets.elasticsearch)
	sed -i.bak s%${string_to_replace}%${secret}%g ${values_file}
}

function put_new_image_in_values {
	local image=$1
	local string_to_replace=$(yaml read ${values_file} image.tag)
	sed -i.bak s%${string_to_replace}%${image}%g ${values_file}
}

cd ../chart/${pipeline_name}

set -x

# Create elasticsearch secret
get_instance_for_bluemix_service $bx_offering_name_elasticsearch
bind_bx_service_instance_to_kubernetes $bx_service_instance_elasticsearch
kube_secret_elasticsearch=$(get_kube_secret "elasticsearch")
put_secret_in_values $kube_secret_elasticsearch 0

# Put new image in ${values_file}
put_new_image_in_values ${image_name}

cd ..

# Package new chart
helm package ${pipeline_name}

# Clone devops repo and put new chart there
git clone git@github.com:${chart_repo}
mv -f *.tgz refarch-cloudnative-devops/docs/edge/
cd refarch-cloudnative-devops/docs

# Reindex repo with new chart information
helm repo index edge

# Publish/commit new changes to chart repo
git add -A && git commit -m "Created new \"${pipeline_name}\" chart from build ${build_number}"

# In here we will trigger the bluecompute deploy pipeline

set +x