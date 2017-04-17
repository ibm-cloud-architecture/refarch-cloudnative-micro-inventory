podTemplate(label: 'mypod',
    volumes: [hostPathVolume(hostPath: '/var/run/docker.sock', mountPath: '/var/run/docker.sock'),
              secretVolume(secretName: 'bx-auth-secret', mountPath: '/var/run/secrets/bx-auth-secret'),
              secretVolume(secretName: 'bluemix-default-secret', mountPath: '/var/run/secrets/bluemix-default-secret')],
    containers: [
        containerTemplate(
            name: 'gradle',
            image: 'fabiogomezdiaz/bc-jenkins-slave:v9',
            alwaysPullImage: true,
            ttyEnabled: true,
            command: 'cat'
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
                sh """
                #!/bin/bash
                # Install plugins
                bx plugin install container-service -r Bluemix
                bx plugin install container-registry -r Bluemix

                # Login to Bluemix and init plugins
                bx login -a api.ng.bluemix.net \
                -u `cat /var/run/secrets/bx-auth-secret/CF_EMAIL` \
                -p `cat /var/run/secrets/bx-auth-secret/CF_PASSWORD` \
                -c `cat /var/run/secrets/bx-auth-secret/CF_ACCOUNT` \
                -o `cat /var/run/secrets/bx-auth-secret/CF_ORG` \
                -s `cat /var/run/secrets/bx-auth-secret/CF_SPACE`

                bx cs init
                bx cr login

                docker tag cloudnative/catalog-fabio-jenkins registry.ng.bluemix.net/chrisking/catalog-fabio-jenkins:${env.BUILD_NUMBER}
                docker push registry.ng.bluemix.net/chrisking/catalog-fabio-jenkins:${env.BUILD_NUMBER}
                """
                //sh "docker tag cloudnative/catalog-fabio-jenkins registry.ng.bluemix.net/chrisking/catalog-fabio-jenkins:${env.BUILD_NUMBER}"
                //sh "docker push registry.ng.bluemix.net/chrisking/catalog-fabio-jenkins:${env.BUILD_NUMBER}"
            }
            stage ('Deploy to Kubernetes') {
                sh """
                #!/bin/bash
                cd catalog
                yaml w -i deployment.yml spec.template.spec.containers[0].image registry.ng.bluemix.net/chrisking/catalog-fabio-jenkins:${env.BUILD_NUMBER}
                kubectl --token=`cat /var/run/secrets/kubernetes.io/serviceaccount/token` create -f deployment.yml
                kubectl --token=`cat /var/run/secrets/kubernetes.io/serviceaccount/token` create -f service.yml
                """
                //sh 'export KUBE_API_TOKEN=`cat /var/run/secrets/kubernetes.io/serviceaccount/token`'
                //sh "cd catalog && yaml w -i deployment.yml spec.template.spec.containers[0].image registry.ng.bluemix.net/chrisking/catalog-fabio-jenkins:${env.BUILD_NUMBER}"
                //sh "cd catalog && kubectl --token=${KUBE_API_TOKEN} create -f deployment.yml"
                //sh "cd catalog && kubectl --token=${KUBE_API_TOKEN} create -f service.yml"
            }
        }
    }
}