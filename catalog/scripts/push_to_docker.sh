#!/bin/bash

build_number=$1
export BLUEMIX_API_KEY=$(cat /var/run/secrets/bx-auth-secret/BLUEMIX_API_KEY)
DOCKER_USER=$(cat /var/run/secrets/bx-auth-secret/DOCKER_USER)
DOCKER_PASS=$(cat /var/run/secrets/bx-auth-secret/DOCKER_PASS)

set -x

# Install plugins
bx plugin install container-service -r Bluemix
bx plugin install container-registry -r Bluemix

# Login to Bluemix and init plugins
bx login
bx cs init
bx cr login

set +x
docker login -u ${DOCKER_USER} -p ${DOCKER_PASS}
set -x
docker tag cloudnative/catalog-jenkins ibmcase/catalog:${build_number}
docker push ibmcase/catalog:${build_number}

set +x