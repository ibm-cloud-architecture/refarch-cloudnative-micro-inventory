#!/bin/bash

# GLOBAL VARIABLES
# Constants
pipeline_name="inventory"
build_number=$1
image_name="registry.ng.bluemix.net/chrisking/${pipeline_name}:${build_number}"
token=$(cat /var/run/secrets/kubernetes.io/serviceaccount/token)
cluster_name=$(cat /var/run/secrets/bx-auth-secret/CLUSTER_NAME)

bx_offering_name_elasticsearch="compose-for-elasticsearch"
bx_offering_name_mysql="compose-for-mysql"
bx_offering_name_messagehub="messagehub"

# To be filled and used for new deployment
bx_service_instance_elasticsearch=""
bx_service_instance_mysql=""
bx_service_instance_messagehub=""

kube_secret_elasticsearch=""
kube_secret_mysql=""
kube_secret_messagehub=""

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

	if [ "$service_name" == "$bx_offering_name_mysql" ]; then
		echo "Found service instance for mysql"
	    bx_service_instance_mysql=$service
	fi

	if [ "$service_name" == "$bx_offering_name_messagehub" ]; then
		echo "Found service instance for messagehub"
	    bx_service_instance_messagehub=$service
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

function put_secret_in_deployment {
	local secret=$1
	local index=$2
	local string_to_replace=$(yaml read deployment.yml spec.template.spec.containers[0].env[${index}].valueFrom.secretKeyRef.name)
	sed -i.bak s%${string_to_replace}%${secret}%g deployment.yml
}

function put_new_image_in_deployment {
	local image=$1
	local string_to_replace=$(yaml read deployment.yml spec.template.spec.containers[0].image)
	sed -i.bak s%${string_to_replace}%${image}%g deployment.yml
}

cd ../kubernetes

set -x

# Create elasticsearch secret
get_instance_for_bluemix_service $bx_offering_name_elasticsearch
bind_bx_service_instance_to_kubernetes $bx_service_instance_elasticsearch
kube_secret_elasticsearch=$(get_kube_secret "elasticsearch")
put_secret_in_deployment $kube_secret_elasticsearch 0

# Create elasticsearch secret
get_instance_for_bluemix_service $bx_offering_name_mysql
bind_bx_service_instance_to_kubernetes $bx_service_instance_mysql
kube_secret_mysql=$(get_kube_secret "mysql")
put_secret_in_deployment $kube_secret_mysql 1

# Create elasticsearch secret
get_instance_for_bluemix_service $bx_offering_name_messagehub
bind_bx_service_instance_to_kubernetes $bx_service_instance_messagehub
kube_secret_messagehub=$(get_kube_secret "messagehub")
put_secret_in_deployment $kube_secret_messagehub 2

# Put new image in deployment.yml
put_new_image_in_deployment ${image_name}

# Check that kubernetes service does not already exist
kube_service=$(get_kube_service_name $pipeline_name)

if [[ -z "${kube_service// }" ]]; then
	# Deploy service
	echo -e "Deploying pipeline_name for the first time"

	# Do the deployment
	kubectl --token=${token} create -f deployment.yml
	kubectl --token=${token} create -f service.yml

else
	# Do rolling update
	echo -e "Doing a rolling update on pipeline_name deployment"
	deployment=$(yaml read deployment.yml metadata.name)
	container=$(yaml read deployment.yml spec.template.spec.containers[0].name)

	kubectl set image deployment/${deployment} ${container}=${image_name}

	# Watch the rollout update
	kubectl --token=${token} rollout status deployment/${deployment}
fi

IP_ADDR=$(get_kube_service_info $kube_service | awk '{print $3}')
if [[ "$IP_ADDR" == "<none>" || -z "${IP_ADDR// }" ]]; then
	IP_ADDR=$(get_kube_service_info $kube_service | awk '{print $2}')
fi

PORT=$(get_kube_service_info $kube_service | awk '{print $4}' | sed 's/:.*//' | sed 's/\/.*//')

echo "View the ${kube_service} at http://$IP_ADDR:$PORT/micro/items"

cd ../scripts
set +x