#!/bin/bash
set -x

BUILD_NUMBER=$1
REGISTRY_NAME=$2
REGISTRY_NAMESPACE=$3

# Init helm
helm init

# Edit chart values using yaml (NEED TO INSTALL YAML) - Call image chart deployer
cd ../chart/bluecompute-catalog

# Replace tag
string_to_replace=$(yaml read values.yaml image.tag)
sed -i.bak s%${string_to_replace}%${BUILD_NUMBER}%g values.yaml

# Replace image repository
string_to_replace=$(yaml read values.yaml image.repository)
sed -i.bak s%${string_to_replace}%${REGISTRY_NAME}/${REGISTRY_NAMESPACE}/bluecompute-catalog%g values.yaml

# Install/Upgrade Chart
cd ..

release=$(helm list | grep catalog | awk '{print $1}' | head -1)

if [[ -z "${release// }" ]]; then
    echo "Installing catalog chart for the first time"
    helm install bluecompute-catalog
else
    echo "Upgrading catalog chart release"
    helm upgrade ${release} bluecompute-catalog
fi