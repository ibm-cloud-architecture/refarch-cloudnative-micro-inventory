/*
    To learn how to use this sample pipeline, follow the guide below and enter the
    corresponding values for your environment and for this repository:
    - https://github.com/ibm-cloud-architecture/refarch-cloudnative-devops-kubernetes
*/

// Pod Template
def podLabel = "inventory"
def cloud = env.CLOUD ?: "kubernetes"
def registryCredsID = env.REGISTRY_CREDENTIALS ?: "registry-credentials-id"
def serviceAccount = env.SERVICE_ACCOUNT ?: "jenkins"

// Pod Environment Variables
def namespace = env.NAMESPACE ?: "default"
def registry = env.REGISTRY ?: "docker.io"
def imageName = env.IMAGE_NAME ?: "ibmcase/bluecompute-inventory"
def deploymentLabels = env.DEPLOYMENT_LABELS ?: "app=inventory,tier=backend,version=v1"
def microServiceName = env.MICROSERVICE_NAME ?: "inventory"
def servicePort = env.MICROSERVICE_PORT ?: "8081"

// External Test Database Parameters
// For username and passwords, set MYSQL_USER (as string parameter) and MYSQL_PASSWORD (as password parameter)
//     - These variables get picked up by the Java application automatically
//     - There were issues with Jenkins credentials plugin interfering with setting up the password directly

def mySQLHost = env.MYSQL_HOST
def mySQLPort = env.MYSQL_PORT ?: "3306"
def mySQLDatabase = env.MYSQL_DATABASE ?: "inventorydb"
def mySQLCredsId = env.MYSQL_CREDENTIALS ?: "inventory-mysql-id"

/*
  Optional Pod Environment Variables
 */
def helmHome = HELM_HOME ?: "/usr/local/bin/helm"

podTemplate(label: podLabel, cloud: cloud, serviceAccount: serviceAccount, namespace: namespace, envVars: [
        envVar(key: 'NAMESPACE', value: namespace),
        envVar(key: 'REGISTRY', value: registry),
        envVar(key: 'IMAGE_NAME', value: imageName),
        envVar(key: 'DEPLOYMENT_LABELS', value: deploymentLabels),
        envVar(key: 'MICROSERVICE_NAME', value: microServiceName),
        envVar(key: 'MICROSERVICE_PORT', value: servicePort),
        envVar(key: 'MYSQL_HOST', value: mySQLHost),
        envVar(key: 'MYSQL_PORT', value: mySQLPort),
        envVar(key: 'MYSQL_DATABASE', value: mySQLDatabase),
        envVar(key: 'HELM_HOME', value: helmHome)
    ],
    volumes: [
        hostPathVolume(hostPath: '/etc/docker/certs.d', mountPath: '/etc/docker/certs.d'),
        hostPathVolume(hostPath: '/var/run/docker.sock', mountPath: '/var/run/docker.sock')
    ],
    containers: [
        containerTemplate(name: 'jdk', image: 'ibmcase/openjdk-bash:latest', ttyEnabled: true, command: 'cat'),
        containerTemplate(name: 'docker' , image: 'ibmcase/docker-bash:latest', ttyEnabled: true, command: 'cat'),
        containerTemplate(name: 'kubectl', image: 'ibmcase/jenkins-slave-utils:latest', ttyEnabled: true, command: 'cat')
  ]) {

    node(podLabel) {
        checkout scm

        // Local
        container(name:'jdk', shell:'/bin/bash') {
            stage('Local - Build and Unit Test') {
                sh """
                #!/bin/bash

                ./gradlew build
                """
            }
            stage('Local - Run and Test') {
                sh """
                #!/bin/bash

                JAVA_OPTS="-Dspring.datasource.url=jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DATABASE}"
                JAVA_OPTS="\${JAVA_OPTS} -Dspring.datasource.port=${MYSQL_PORT}"
                JAVA_OPTS="\${JAVA_OPTS} -Dserver.port=${MICROSERVICE_PORT}"

                java \${JAVA_OPTS} -jar build/libs/micro-inventory-0.0.1.jar &

                # Let the application start
                sleep 25

                # Run tests
                set -x
                bash scripts/api_tests.sh 127.0.0.1 ${MICROSERVICE_PORT}
                set +x;
                """
            }
        }

        // Docker
        container(name:'docker', shell:'/bin/bash') {
            stage('Docker - Build Image') {
                sh """
                #!/bin/bash

                # Get image
                if [ "${REGISTRY}" = "docker.io" ]; then
                    IMAGE=${IMAGE_NAME}:${env.BUILD_NUMBER}
                else
                    IMAGE=${REGISTRY}/${NAMESPACE}/${IMAGE_NAME}:${env.BUILD_NUMBER}
                fi

                docker build -t \${IMAGE} .
                """
            }
            stage('Docker - Run and Test') {
                withCredentials([usernamePassword(credentialsId: mySQLCredsId,
                                               usernameVariable: 'MYSQL_USER',
                                               passwordVariable: 'MYSQL_PASSWORD')]) {
                    sh """
                    #!/bin/bash

                    # Get image
                    if [ "${REGISTRY}" = "docker.io" ]; then
                        IMAGE=${IMAGE_NAME}:${env.BUILD_NUMBER}
                    else
                        IMAGE=${REGISTRY}/${NAMESPACE}/${IMAGE_NAME}:${env.BUILD_NUMBER}
                    fi

                    # Kill Container if it already exists
                    docker kill ${MICROSERVICE_NAME} || true
                    docker rm ${MICROSERVICE_NAME} || true

                    # Start Container
                    docker run --name ${MICROSERVICE_NAME} -d -p ${MICROSERVICE_PORT}:${MICROSERVICE_PORT} \
                        -e SERVICE_PORT=${MICROSERVICE_PORT} \
                        -e MYSQL_HOST=${MYSQL_HOST} \
                        -e MYSQL_PORT=${MYSQL_PORT} \
                        -e MYSQL_USER=${MYSQL_USER} \
                        -e MYSQL_PASSWORD=${MYSQL_PASSWORD} \
                        -e MYSQL_DATABASE=${MYSQL_DATABASE} \${IMAGE}

                    # Let the application start
                    sleep 25

                    # Check that application started successfully
                    docker ps

                    # Check the logs
                    docker logs ${MICROSERVICE_NAME}

                    # Run tests
                    bash scripts/api_tests.sh 127.0.0.1 ${MICROSERVICE_PORT}

                    # Kill Container
                    docker kill ${MICROSERVICE_NAME} || true
                    docker rm ${MICROSERVICE_NAME} || true
                    """
                }
            }
            stage('Docker - Push Image to Registry') {
                withCredentials([usernamePassword(credentialsId: registryCredsID,
                                               usernameVariable: 'USERNAME',
                                               passwordVariable: 'PASSWORD')]) {
                    sh """
                    #!/bin/bash

                    # Get image
                    if [ "${REGISTRY}" = "docker.io" ]; then
                        IMAGE=${IMAGE_NAME}:${env.BUILD_NUMBER}
                    else
                        IMAGE=${REGISTRY}/${NAMESPACE}/${IMAGE_NAME}:${env.BUILD_NUMBER}
                    fi

                    docker login -u ${USERNAME} -p ${PASSWORD} ${REGISTRY}

                    docker push \${IMAGE}
                    """
                }
            }
        }

        // Kubernetes
        container(name:'kubectl', shell:'/bin/bash') {
            stage('Initialize CLIs') {
                sh """
                echo "Initializing Helm ..."
                helm init -c
                """
            }
            stage('Kubernetes - Deploy new Docker Image') {
                withCredentials([usernamePassword(credentialsId: mySQLCredsId,
                                               usernameVariable: 'MYSQL_USER',
                                               passwordVariable: 'MYSQL_PASSWORD')]) {
                    sh """
                    #!/bin/bash

                    # Get image
                    if [ "${REGISTRY}" = "docker.io" ]; then
                        IMAGE=${IMAGE_NAME}
                    else
                        IMAGE=${REGISTRY}/${NAMESPACE}/${IMAGE_NAME}
                    fi

                    # Build PARAMETERS
                    PARAMETERS="--set image.repository="\${IMAGE}"
                    PARAMETERS="\${PARAMETERS} --set image.tag=${env.BUILD_NUMBER}"
                    PARAMETERS="\${PARAMETERS} --set mysql.host=${MYSQL_HOST}"
                    PARAMETERS="\${PARAMETERS} --set mysql.port=${MYSQL_PORT}"
                    PARAMETERS="\${PARAMETERS} --set mysql.database=${MYSQL_DATABASE}"
                    PARAMETERS="\${PARAMETERS} --set mysql.user=${MYSQL_USER}"
                    PARAMETERS="\${PARAMETERS} --set mysql.password=${MYSQL_PASSWORD}"

                    helm upgrade --install ${MICROSERVICE_NAME} "\${PARAMETERS}" chart/${MICROSERVICE_NAME} --wait --tls
                    """
                }
            }
            stage('Kubernetes - Test') {
                sh """
                #!/bin/bash

                # Get deployment
                DEPLOYMENT=`kubectl --namespace=${NAMESPACE} get deployments -l ${DEPLOYMENT_LABELS} -o name`

                # Wait for deployment to start accepting connections
                sleep 35

                # Check the logs
                kubectl port-forward \${DEPLOYMENT} ${MICROSERVICE_PORT}:${MICROSERVICE_PORT} &

                # Run tests
                bash scripts/api_tests.sh 127.0.0.1 ${MICROSERVICE_PORT}

                # Kill port forwarding
                killall kubectl || true
                """
            }
        }
    }
}