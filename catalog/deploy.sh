#!/bin/bash

function get_elastic_secret {
	echo $(kubectl get secrets | grep "compose-for-elasticsearch" | awk '{print $1}')
}

echo "Create Guestbook"
IP_ADDR=$(bx cs workers $CLUSTER_NAME | grep deployed | awk '{ print $2 }')
if [ -z $IP_ADDR ]; then
  echo "$CLUSTER_NAME not created or workers not ready"
  exit 1
fi

echo -e "Configuring vars"
exp=$(bx cs cluster-config $CLUSTER_NAME | grep export)
if [ $? -ne 0 ]; then
  echo "Cluster $CLUSTER_NAME not created or not ready."
  exit 1
fi
eval "$exp"

# Check if elasticsearch secret exists
elastic_secret=$(get_elastic_secret)

if [[ -z "${elastic_secret// }" ]]; then
	echo "elasticsearch secret does not exist. Creating"
	elastic_service="\"$(cf services | grep "compose-for-elasticsearch" | head -1 | sed -e 's/compose-for-elasticsearch.*//' | sed 's/[[:blank:]]*$//')\""

	if [[ -z "${elastic_secret// }" ]]; then
		echo "Cannot create secret. No service instance exists for compose-for-elasticsearch."
		exit 1
	fi

	echo "Creating secret from ${elastic_service}"
	bx cs cluster-service-bind $CLUSTER_NAME default $elastic_service

	if [ $? -ne 0 ]; then
	  echo "Could not create secret for ${elastic_service} service."
	  exit 1
	fi

	elastic_secret=$(get_elastic_secret)

	if [[ -z "${elastic_secret// }" ]]; then
		echo "Cannot retrieve secret for ${elastic_service} service."
		exit 1
	fi
fi

cat catalog/deployment.yml
# Enter secret and image name into yaml
sed -i.bak s%binding-compose-for-elasticsearch%${elastic_secret}%g catalog/deployment.yml
sed -i.bak s%registry.ng.bluemix.net/chrisking/catalog:v1%${IMAGE_NAME}%g catalog/deployment.yml
cat catalog/deployment.yml

# Delete previous service
echo -e "Deleting previous version of catalog if it exists"
kubectl delete --ignore-not-found=true -f catalog/service.yml
kubectl delete --ignore-not-found=true -f catalog/deployment.yml

# Deploy service
echo -e "Creating pods"
kubectl create -f catalog/deployment.yml
kubectl create -f catalog/service.yml

PORT=$(kubectl get services | grep frontend | sed 's/.*://g' | sed 's/\/.*//g')

echo "View the guestbook at http://$IP_ADDR:$PORT"