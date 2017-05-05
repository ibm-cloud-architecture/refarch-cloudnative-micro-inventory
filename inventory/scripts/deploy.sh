#!/bin/bash
set -x

BUILD_NUMBER=$1
REGISTRY_NAME=$2
REGISTRY_NAMESPACE=$3

# Init helm
helm init

# Edit chart values using yaml (NEED TO INSTALL YAML) - Call image chart deployer
cd ../chart/bluecompute-inventory

# Replace tag
string_to_replace=$(yaml read values.yaml image.tag)
sed -i.bak s%${string_to_replace}%${BUILD_NUMBER}%g values.yaml

# Replace image repository
string_to_replace=$(yaml read values.yaml image.repository)
sed -i.bak s%${string_to_replace}%${REGISTRY_NAME}/${REGISTRY_NAMESPACE}/bluecompute-inventory%g values.yaml

# Install/Upgrade Chart
cd ..

release=$(helm list | grep inventory | awk '{print $1}' | head -1)

if [[ -z "${release// }" ]]; then
    echo "Installing inventory chart for the first time"
    helm install bluecompute-inventory
else
    echo "Upgrading inventory chart release"
    helm upgrade ${release} bluecompute-inventory
fi