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
                sh 'printenv'
                sh 'export KUBE_API_TOKEN=$(cat /var/run/secrets/kubernetes.io/serviceaccount/token)'
                sh "export REGISTRY_NAMESPACE=$(bx cr namespace-list | egrep -v 'Listing namespaces...' | egrep -v '^OK$' | sed -e '/^Namespace   $/d' | sed -e '/^\s*$/d' | tr -d '[:space:]')"
                sh 'printenv'
                //sh 'docker info'
                sh 'cd catalog && ./gradlew build -x test'
            }
            stage ('Build Docker Image') {
                sh 'cd catalog && ./gradlew docker && cd docker && docker build -t cloudnative/catalog-fabio-jenkins .'
            }
            stage ('Push Docker Image to Registry') {
                sh 'docker tag cloudnative/catalog-fabio-jenkins registry.ng.bluemix.net/${REGISTRY_NAMESPACE}/catalog-fabio-jenkins:${env.BUILD_NUMBER}'
                sh 'docker push registry.ng.bluemix.net/${REGISTRY_NAMESPACE}/catalog-fabio-jenkins:${env.BUILD_NUMBER}'
            }
        }


        /*stage 'Build and push Docker Image'
        git 'https://github.com/fabiogomezdiaz/refarch-cloudnative-micro-inventory'
        container('gradle') {
            stage 'Build gradle project'
            sh """#!/bin/bash -l
            cd catalog
            ./gradlew build -x test
            cd ..
            """

            stage 'Build docker image'
            sh """#!/bin/bash -l
            cd catalog
            ./gradlew docker
            cd docker
            docker build -t cloudnative/catalog-fabio-jenkins .
            cd ..
            """

            stage 'Push docker image'
            sh """#!/bin/bash -l
            docker tag cloudnative/catalog-fabio-jenkins registry.ng.bluemix.net/\$(cf ic namespace get)/:v1
            docker push registry.ng.bluemix.net/\$(cf ic namespace get)/catalog-fabio-jenkins:${env.BUILD_NUMBER}
            """
        }*/
    }
}