podTemplate(label: 'mypod',
    volumes: [hostPathVolume(hostPath: '/var/run/docker.sock', mountPath: '/var/run/docker.sock')],
    containers: [
        containerTemplate(
            name: 'gradle',
            image: 'fabiogomezdiaz/bc-jenkins-slave:v3',
            ttyEnabled: true,
            command: 'cat',
            envVars: [
                containerEnvVar(key: 'CF_EMAIL', value: '${env.CF_EMAIL}'),
                containerEnvVar(key: 'CF_PASSWORD', value: '${env.CF_PASSWORD}'),
                containerEnvVar(key: 'CF_ORG', value: '${env.CF_ORG}'),
                containerEnvVar(key: 'CF_SPACE', value: '${env.CF_SPACE}'),
                containerEnvVar(key: 'CF_ACCOUNT', value: '${env.CF_ACCOUNT}'),
                containerEnvVar(key: 'CLUSTER_NAME', value: '${env.CLUSTER_NAME}')
            ])
    ]) {

    node ('mypod') {
        container('gradle') {
            stage ('Build') {
                checkout scm
                sh 'printenv'
                sh 'docker info'
                sh 'cd catalog && ./gradlew build -x test'
            }
            stage ('Build Docker Image') {
                sh 'cd catalog && ./gradlew docker && cd docker && docker build -t cloudnative/catalog-fabio-jenkins .'
            }
            stage ('Push Docker Image to Registry') {
                sh 'docker tag cloudnative/catalog-fabio-jenkins registry.ng.bluemix.net/\$(cf ic namespace get)/catalog-fabio-jenkins:${env.BUILD_NUMBER}'
                sh 'docker push registry.ng.bluemix.net/\$(cf ic namespace get)/catalog-fabio-jenkins:${env.BUILD_NUMBER}'
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