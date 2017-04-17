podTemplate(label: 'mypod',
    volumes: [hostPathVolume(hostPath: '/var/run/docker.sock', mountPath: '/var/run/docker.sock'),
              secretVolume(secretName: 'bx-auth-secret', mountPath: '/var/run/secrets/bx-auth-secret')],
    containers: [
        containerTemplate(
            name: 'gradle',
            image: 'fabiogomezdiaz/bc-jenkins-slave:v6',
            alwaysPullImage: true,
            ttyEnabled: true,
            command: 'cat'
    )]) {

    node ('mypod') {
        container('gradle') {
            stage ('Build') {
                checkout scm
                sh '''
                set -x
                printenv
                export KUBE_API_TOKEN=`cat /var/run/secrets/kubernetes.io/serviceaccount/token`
                echo ${KUBE_API_TOKEN}
                export REGISTRY_NAMESPACE=`bx cr namespace-list | egrep -v '(^Listing namespaces...$|^OK$|^Namespace   $)' | tr -d '[:space:]'`
                printenv
                cd catalog && ./gradlew build -x test
                '''
            }
            stage ('Build Docker Image') {
                sh 'cd catalog && ./gradlew docker && cd docker && docker build -t cloudnative/catalog-fabio-jenkins .'
            }
            stage ('Push Docker Image to Registry') {
                sh 'docker tag cloudnative/catalog-fabio-jenkins registry.ng.bluemix.net/${REGISTRY_NAMESPACE}/catalog-fabio-jenkins:${env.BUILD_NUMBER}'
                sh 'docker push registry.ng.bluemix.net/${REGISTRY_NAMESPACE}/catalog-fabio-jenkins:${env.BUILD_NUMBER}'
            }
        }
    }
}