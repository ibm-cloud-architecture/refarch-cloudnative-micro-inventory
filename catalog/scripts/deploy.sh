#!/bin/bash
set -x

BUILD_NUMBER=$1
REGISTRY_NAME=$2
REGISTRY_NAMESPACE=$3
ORG=$4
SPACE=$5
CLUSTER_NAME=$6
API_KEY=$7

# Init helm
helm init

# Edit chart values using yaml (NEED TO INSTALL YAML) - Call image chart deployer
cd ../chart/bluecompute-catalog

# Install/Upgrade Chart
release=$(helm list | grep bluecompute-catalog | awk '{print $1}' | head -1)

if [[ -z "${release// }" ]]; then
    echo "Installing bluecompute-catalog chart for the first time"
    time helm install --name catalog \
    --set image.tag=${BUILD_NUMBER} \
    --set image.repository=${REGISTRY_NAME}/${REGISTRY_NAMESPACE}/bluecompute-catalog \
	--set configMap.bluemixOrg=${ORG} \
	--set configMap.bluemixSpace=${SPACE} \
	--set configMap.kubeClusterName=${CLUSTER_NAME} \
	--set secret.apiKey=${API_KEY} \
	. --debug --wait --timeout 600

else
    echo "Upgrading bluecompute-catalog chart release"
    time helm upgrade catalog \
    --set image.tag=${BUILD_NUMBER} \
    --set image.repository=${REGISTRY_NAME}/${REGISTRY_NAMESPACE}/bluecompute-catalog \
	--set configMap.bluemixOrg=${ORG} \
	--set configMap.bluemixSpace=${SPACE} \
	--set configMap.kubeClusterName=${CLUSTER_NAME} \
	--set secret.apiKey=${API_KEY} \
	. --debug --wait --timeout 600
fi