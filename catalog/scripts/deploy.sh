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
cd ../chart/catalog

release=$(helm list | grep catalog | awk '{print $1}' | head -1)

if [[ -z "${release// }" ]]; then
    echo "Installing bluecompute-catalog chart for the first time"
    time helm install \
        --name catalog \
        . \
        --debug \
        --wait \
        --timeout 600 \
        --set image.tag=${BUILD_NUMBER} \
        --set image.repository=${BX_REGISTRY}/${BX_CR_NAMESPACE}/bluecompute-catalog \
        --set configMap.skipDelete=true \
        --set secret.skipDelete=true \
        --set configMap.bluemixOrg=${BX_ORG} \
        --set configMap.bluemixSpace=${BX_SPACE} \
        --set configMap.bluemixRegistryNamespace=${BX_CR_NAMESPACE} \
        --set configMap.kubeClusterName=${CLUSTER_NAME} \
        --set secret.apiKey=${BX_API_KEY}

else
    echo "Upgrading bluecompute-catalog chart release"
    time helm upgrade catalog . \
        --reuse-values \
        --set image.tag=${BUILD_NUMBER} \
        --set image.repository=${BX_REGISTRY}/${BX_CR_NAMESPACE}/bluecompute-catalog \
        --set configMap.skipDelete=true \
        --set secret.skipDelete=true \
        --set configMap.bluemixOrg=${BX_ORG} \
        --set configMap.bluemixSpace=${BX_SPACE} \
        --set configMap.bluemixRegistryNamespace=${BX_CR_NAMESPACE} \
        --set configMap.kubeClusterName=${CLUSTER_NAME} \
        --set secret.apiKey=${BX_API_KEY}
        --debug \
        --wait \
        --timeout 600
fi
