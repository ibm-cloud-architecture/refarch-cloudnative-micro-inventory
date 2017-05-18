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
cd ../chart/bluecompute-catalog

# Replace values
cat values.yaml | \
    yaml w - image.tag ${BUILD_NUMBER} | \
    yaml w - image.repository ${BX_REGISTRY}/${BX_CR_NAMESPACE}/bluecompute-catalog | \
    yaml w - hs256key.skipDelete true | \
    yaml w - configMap.skipDelete true | \
    yaml w - secret.skipDelete true | \
    yaml w - configMap.bluemixOrg ${BX_ORG} | \
    yaml w - configMap.bluemixSpace ${BX_SPACE} | \
    yaml w - configMap.bluemixRegistryNamespace ${BX_CR_NAMESPACE} | \
    yaml w - configMap.kubeClusterName ${CLUSTER_NAME} | \
    yaml w - secret.apiKey ${BX_API_KEY} > \
        values_new.yaml

mv values_new.yaml values.yaml

release=$(helm list | grep catalog | awk '{print $1}' | head -1)

if [[ -z "${release// }" ]]; then
    echo "Installing bluecompute-catalog chart for the first time"
    time helm install --name catalog . --debug --wait --timeout 600
else
    echo "Upgrading bluecompute-catalog chart release"
    time helm upgrade catalog . --debug --wait --timeout 600
fi