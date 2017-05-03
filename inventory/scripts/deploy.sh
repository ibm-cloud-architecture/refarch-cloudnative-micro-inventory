#!/bin/bash
set -x

build_number=$1
helm init

# Edit chart values using yaml (NEED TO INSTALL YAML) - Call image chart deployer
cd ../chart/bc-inventory
string_to_replace=$(yaml read values.yaml image.tag)
sed -i.bak s%${string_to_replace}%${build_number}%g values.yaml

# Install/Upgrade Chart
cd ..

release=$(helm list | grep inventory | awk '{print $1}' | head -1)

if [[ -z "${release// }" ]]; then
    echo "Installing inventory chart for the first time"
    helm install bc-inventory
else
    echo "Upgrading inventory chart release"
    helm upgrade ${release} bc-inventory
fi