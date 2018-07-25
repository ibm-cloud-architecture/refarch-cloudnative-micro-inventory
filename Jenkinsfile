// Pod Template
def podLabel = "inventory"
def cloud = env.CLOUD ?: "kubernetes"
def registryCredsID = env.REGISTRY_CREDENTIALS ?: "registry-credentials-id"
def serviceAccount = env.SERVICE_ACCOUNT ?: "default"

// Pod Environment Variables
def namespace = env.NAMESPACE ?: "default"
def registry = env.REGISTRY ?: "mycluster.icp:8500"
def imageName = env.IMAGE_NAME ?: "bluecompute-inventory"
def deploymentLabels = env.DEPLOYMENT_LABELS ?: "app=bluecompute,tier=backend,micro=inventory"
def podName = env.POD_NAME ?: "inventory"

podTemplate(label: podLabel, cloud: cloud, serviceAccount: serviceAccount, namespace: namespace, envVars: [
        envVar(key: 'NAMESPACE', value: namespace),
        envVar(key: 'REGISTRY', value: registry),
        envVar(key: 'IMAGE_NAME', value: imageName),
        envVar(key: 'DEPLOYMENT_LABELS', value: deploymentLabels),
        envVar(key: 'POD_NAME', value: podName)
    ],
    volumes: [
        hostPathVolume(hostPath: '/etc/docker/certs.d', mountPath: '/etc/docker/certs.d'),
        hostPathVolume(hostPath: '/var/run/docker.sock', mountPath: '/var/run/docker.sock')
    ],
    containers: [
        containerTemplate(name: 'kubectl', image: 'lachlanevenson/k8s-kubectl', ttyEnabled: true, command: 'cat'),
        containerTemplate(name: 'docker' , image: 'docker:17.06.1-ce', ttyEnabled: true, command: 'cat')
  ]) {

    node(podLabel) {
        checkout scm
        container('docker') {
            stage('Build Docker Image') {
                sh """
                #!/bin/bash
                docker build -t ${env.REGISTRY}/${env.NAMESPACE}/${env.IMAGE_NAME}:${env.BUILD_NUMBER} .
                """
            }
            stage('Push Docker Image to Registry') {
                withCredentials([usernamePassword(credentialsId: registryCredsID,
                                               usernameVariable: 'USERNAME',
                                               passwordVariable: 'PASSWORD')]) {
                    sh """
                    #!/bin/bash
                    docker login -u ${USERNAME} -p ${PASSWORD} ${env.REGISTRY}
                    docker push ${env.REGISTRY}/${env.NAMESPACE}/${env.IMAGE_NAME}:${env.BUILD_NUMBER}
                    """
                }
            }
        }
        container('kubectl') {
            stage('Deploy new Docker Image') {
                sh """
                #!/bin/bash
                DEPLOYMENT=`kubectl --namespace=${env.NAMESPACE} get deployments -l ${env.DEPLOYMENT_LABELS} -o name`
                kubectl --namespace=${env.NAMESPACE} get \${DEPLOYMENT}
                if [ \${?} -ne "0" ]; then
                    # No deployment to update
                    echo 'No deployment to update'
                    exit 1
                fi
                # Update Deployment
                kubectl --namespace=${env.NAMESPACE} set image \${DEPLOYMENT} ${env.POD_NAME}=${env.REGISTRY}/${env.NAMESPACE}/${env.IMAGE_NAME}:${env.BUILD_NUMBER}
                kubectl --namespace=${env.NAMESPACE} rollout status \${DEPLOYMENT}
                """
            }
        }
    }
}