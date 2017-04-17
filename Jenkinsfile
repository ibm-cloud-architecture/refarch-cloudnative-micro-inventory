podTemplate(label: 'mypod',
    volumes: [hostPathVolume(hostPath: '/var/run/docker.sock', mountPath: '/var/run/docker.sock'),
              secretVolume(secretName: 'bx-auth-secret', mountPath: '/var/run/secrets/bx-auth-secret')],
    containers: [
        containerTemplate(
            name: 'gradle',
            image: 'fabiogomezdiaz/bc-jenkins-slave:v9',
            alwaysPullImage: true,
            ttyEnabled: true,
            command: 'cat',
            envVars: [containerEnvVar(key: 'REGISTRY_NAMESPACE', value: 'chrisking')]
    )]) {

    node ('mypod') {
        container('gradle') {
            stage ('Build') {
                checkout scm
                sh 'cd catalog && ./gradlew build -x test'
            }
            stage ('Build Docker Image') {
                sh 'cd catalog && ./gradlew docker && cd docker && docker build -t cloudnative/catalog-fabio-jenkins .'
            }
            stage ('Push Docker Image to Registry') {
                sh "docker tag cloudnative/catalog-fabio-jenkins registry.ng.bluemix.net/${REGISTRY_NAMESPACE}/catalog-fabio-jenkins:${env.BUILD_NUMBER}"
                sh "docker push registry.ng.bluemix.net/${REGISTRY_NAMESPACE}/catalog-fabio-jenkins:${env.BUILD_NUMBER}"
            }
            stage ('Deploy to Kubernetes') {
                sh 'export KUBE_API_TOKEN=`cat /var/run/secrets/kubernetes.io/serviceaccount/token`'
                sh "cd catalog && yaml w -i deployment.yml spec.template.spec.containers[0].image registry.ng.bluemix.net/${REGISTRY_NAMESPACE}/catalog-fabio-jenkins:${env.BUILD_NUMBER}"
                sh "cd catalog && kubectl --token=${KUBE_API_TOKEN} create -f deployment.yml"
                sh "cd catalog && kubectl --token=${KUBE_API_TOKEN} create -f service.yml"
            }
        }
    }
}