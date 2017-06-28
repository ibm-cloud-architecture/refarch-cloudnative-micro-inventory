#!/bin/bash
set -x

BUILD_NUMBER=$1
BX_REGISTRY=$2
BX_CR_NAMESPACE=$3
BX_ORG=$4
BX_SPACE=$5
CLUSTER_NAME=$6
BX_API_KEY=$7

# Init helm
helm init

# Install/Upgrade Chart
cd ../chart/inventory

release=$(helm list | grep inventory | awk '{print $1}' | head -1)

if [[ -z "${release// }" ]]; then
    echo "Installing bluecompute-inventory chart for the first time"
    time helm install --name inventory . --debug --wait --timeout 600 \
        --set image.tag=${BUILD_NUMBER} \
        --set image.repository=${BX_REGISTRY}/${BX_CR_NAMESPACE}/bluecompute-inventory \
        --set configMap.skipDelete=true \
        --set secret.skipDelete=true \
        --set configMap.bluemixOrg=${BX_ORG} \
        --set configMap.bluemixSpace=${BX_SPACE} \
        --set configMap.bluemixRegistryNamespace=${BX_CR_NAMESPACE} \
        --set configMap.kubeClusterName=${CLUSTER_NAME} \
        --set secret.apiKey=${BX_API_KEY} 

else
    echo "Upgrading bluecompute-inventory chart release"
    time helm upgrade inventory . --reuse-values --debug --wait --timeout 600 \ 
        --set image.tag=${BUILD_NUMBER} \
        --set image.repository=${BX_REGISTRY}/${BX_CR_NAMESPACE}/bluecompute-inventory \
        --set configMap.skipDelete=true \
        --set secret.skipDelete=true \
        --set configMap.bluemixOrg=${BX_ORG} \
        --set configMap.bluemixSpace=${BX_SPACE} \
        --set configMap.bluemixRegistryNamespace=${BX_CR_NAMESPACE} \
        --set configMap.kubeClusterName=${CLUSTER_NAME} \
        --set secret.apiKey=${BX_API_KEY} 

fi
