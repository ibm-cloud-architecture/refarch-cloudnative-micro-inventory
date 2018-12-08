###### refarch-cloudnative-micro-inventory

## Microservice Apps Integration with ElasticSearch and MySQL Database

[![Build Status](https://travis-ci.org/ibm-cloud-architecture/refarch-cloudnative-micro-inventory.svg?branch=master)](https://travis-ci.org/ibm-cloud-architecture/refarch-cloudnative-micro-inventory)

*This project is part of the 'IBM Cloud Native Reference Architecture' suite, available at
https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes/tree/spring*

## Table of Contents
* [Introduction](#introduction)
    + [APIs](#apis)
* [Pre-requisites:](#pre-requisites)
* [Deploy Inventory Application to Kubernetes Cluster](#deploy-inventory-application-to-kubernetes-cluster)
* [Deploy Inventory Application on Docker](#deploy-inventory-application-on-docker)
    + [Deploy the MySQL Docker Container](#deploy-the-mysql-docker-container)
    + [Populate the MySQL Database](#populate-the-mysql-database)
    + [Deploy the Inventory Docker Container](#deploy-the-inventory-docker-container)
* [Run Inventory Service application on localhost](#run-inventory-service-application-on-localhost)
* [Deploy Inventory Application on OpenLiberty](#deploy-inventory-application-on-openliberty)
* [Optional: Setup CI/CD Pipeline](#optional-setup-cicd-pipeline)
* [Conclusion](#conclusion)
* [Contributing](#contributing)
    + [GOTCHAs](#gotchas)
    + [Contributing a New Chart Package to Microservices Reference Architecture Helm Repository](#contributing-a-new-chart-package-to-microservices-reference-architecture-helm-repository)

## Introduction
This project will demonstrate how to deploy a Spring Boot Application with a MySQL database onto a Kubernetes Cluster.

![Application Architecture](static/inventory.png?raw=true)

Here is an overview of the project's features:
- Leverage [`Spring Boot`](https://projects.spring.io/spring-boot/) framework to build a Microservices application.
- Uses [`Spring Data JPA`](http://projects.spring.io/spring-data-jpa/) to persist data to MySQL database.
- Uses [`MySQL`](https://www.mysql.com/) as the inventory database.
- Uses [`Docker`](https://docs.docker.com/) to package application binary and its dependencies.
- Uses [`Helm`](https://helm.sh/) to package application and MySQL deployment configuration and deploy to a [`Kubernetes`](https://kubernetes.io/) cluster.

### APIs
* Get all items in inventory:
    + `http://localhost:8080/micro/inventory`

## Pre-requisites:
* Create a Kubernetes Cluster by following the steps [here](https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes#create-a-kubernetes-cluster).
* Install the following CLI's on your laptop/workstation:
    + [`docker`](https://docs.docker.com/install/)
    + [`kubectl`](https://kubernetes.io/docs/tasks/tools/install-kubectl/)
    + [`helm`](https://docs.helm.sh/using_helm/#installing-helm)
* Clone inventory repository:
```bash
git clone -b spring --single-branch https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory.git
cd refarch-cloudnative-micro-inventory
```

## Deploy Inventory Application to Kubernetes Cluster
In this section, we are going to deploy the Inventory Application, along with a MySQL service, to a Kubernetes cluster using Helm. To do so, follow the instructions below:
```bash
# Install MySQL Chart
helm upgrade --install mysql \
  --version 0.10.2 \
  --set fullnameOverride=inventory-mysql \
  --set mysqlRootPassword=admin123 \
  --set mysqlUser=dbuser \
  --set mysqlPassword=password \
  --set mysqlDatabase=inventorydb \
  --set persistence.enabled=false \
  stable/mysql

# Go to Chart Directory
cd chart/inventory

# Deploy Inventory to Kubernetes cluster
helm upgrade --install inventory --set service.type=NodePort,mysql.existingSecret=inventory-mysql .
```

The last command will give you instructions on how to access/test the Inventory application. Please note that before the Inventory application starts, the MySQL deployment must be fully up and running, which normally takes a couple of minutes. With Kubernetes [Init Containers](https://kubernetes.io/docs/concepts/workloads/pods/init-containers/), the Inventory Deployment polls for MySQL readiness status so that Inventory can start once MySQL is ready, or error out if MySQL fails to start.

Also, once MySQL is fully up and running, a [`Kubernetes Job`](https://kubernetes.io/docs/concepts/workloads/controllers/jobs-run-to-completion/) will run to populate the MySQL database with the inventory data so that it can be served by the application. This is done for convenience as the inventory data is static.

To check and wait for the deployment status, you can run the following command:
```bash
kubectl get deployments -w
NAME                  DESIRED   CURRENT   UP-TO-DATE   AVAILABLE   AGE
inventory-inventory   1         1         1            1           10h
```

The `-w` flag is so that the command above not only retrieves the deployment but also listens for changes. If you a 1 under the `CURRENT` column, that means that the inventory app deployment is ready.

## Deploy Inventory Application on Docker
You can also run the Inventory Application locally on Docker. Before we show you how to do so, you will need to have a running MySQL deployment running somewhere.

### Deploy the MySQL Docker Container
The easiest way to get MySQL running is via a Docker container. To do so, run the following commands:
```bash
# Start a MySQL Container with a database user, a password, and create a new database
docker run --name inventorymysql \
    -e MYSQL_ROOT_PASSWORD=admin123 \
    -e MYSQL_USER=dbuser \
    -e MYSQL_PASSWORD=password \
    -e MYSQL_DATABASE=inventorydb \
    -p 3306:3306 \
    -d mysql:5.7.14

# Get the MySQL Container's IP Address
docker inspect inventorymysql | grep "IPAddress"
            "SecondaryIPAddresses": null,
            "IPAddress": "172.17.0.2",
                    "IPAddress": "172.17.0.2",
```
Make sure to select the IP Address in the `IPAddress` field. You will use this IP address when deploying the Inventory container.

### Populate the MySQL Database
In order for Inventory to make use of the MySQL database, the database needs to be populated first. To do so, run the following commands:
```bash
until mysql -h 127.0.0.1 -P 3306 -udbuser -ppassword <./scripts/mysql_data.sql; do echo "waiting for mysql"; sleep 1; done; echo "Loaded data into database"
```

Note that we didn't use the IP address we obtained from the MySQL since it is only accessible to other Docker Containers. We used `127.0.0.1` localhost IP address instead since we mapped the 3306 port on the docker container to the 3306 port in localhost.

### Deploy the Inventory Docker Container
To deploy the Inventory container, run the following commands:
```bash
# Build the Docker Image
docker build -t inventory .

# Start the Inventory Container
docker run --name inventory \
    -e MYSQL_HOST=${MYSQL_IP_ADDRESS} \
    -e MYSQL_PORT=3306 \
    -e MYSQL_USER=dbuser \
    -e MYSQL_PASSWORD=password \
    -e MYSQL_DATABASE=inventorydb \
    -p 8080:8080 \
    -d inventory
```

Where `${MYSQL_IP_ADDRESS}` is the IP address of the MySQL container, which is only accessible from the Docker container network.

If everything works successfully, you should be able to get some data when you run the following command:
```bash
curl http://localhost:8080/micro/inventory
```

## Run Inventory Service application on localhost
In this section you will run the Spring Boot application on your local workstation. Before we show you how to do so, you will need to deploy a MySQL Docker container and populate it with data as shown in the [Deploy a MySQL Docker Container](#deploy-a-mysql-docker-container) and [Populate the MySQL Database](#populate-the-mysql-database) sections, respectively.

Once MySQL is ready and populated, we can run the Spring Boot Inventory application locally as follows:

1. Open [`src/main/resources/application.yml`](src/main/resources/application.yml) file, enter the following values for the fields under `spring.datasource`, and save the file:
    * **url:** jdbc:mysql://127.0.0.1:3306/inventorydb
    * **username:** dbuser
    * **password:** password
    * **port:** 3306

2. Build the application:
```bash
./gradlew build -x test
```

3. Run the application on localhost:
```bash
java -jar build/libs/micro-inventory-0.0.1.jar
```

4. Validate. You should get a list of all inventory items:
```bash
curl http://localhost:8080/micro/inventory
```

That's it, you have successfully deployed and tested the Inventory microservice.

## Deploy Inventory Application on Open Liberty

The Spring Boot applications can be deployed on WebSphere Liberty as well. In this case, the embedded server i.e. the application server packaged up in the JAR file will be Liberty. For instructions on how to deploy the Inventory application optimized for Docker on Open Liberty, which is the open source foundation for WebSphere Liberty, follow the instructions [here](OpenLiberty.MD).

## Optional: Setup CI/CD Pipeline
If you would like to setup an automated Jenkins CI/CD Pipeline for this repository, we provided a sample [Jenkinsfile](Jenkinsfile), which uses the [Jenkins Pipeline](https://jenkins.io/doc/book/pipeline/) syntax of the [Jenkins Kubernetes Plugin](https://github.com/jenkinsci/kubernetes-plugin) to automatically create and run Jenkis Pipelines from your Kubernetes environment.

To learn how to use this sample pipeline, follow the guide below and enter the corresponding values for your environment and for this repository:
* https://github.com/ibm-cloud-architecture/refarch-cloudnative-devops-kubernetes

## Conclusion
You have successfully deployed and tested the Inventory Microservice and a MySQL database both on a Kubernetes Cluster and in local Docker Containers.

To see the Inventory app working in a more complex microservices use case, checkout our Microservice Reference Architecture Application [here](https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes/tree/spring).

## Contributing
If you would like to contribute to this repository, please fork it, submit a PR, and assign as reviewers any of the GitHub users listed here:
* https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/graphs/contributors

### GOTCHAs
1. We use [Travis CI](https://travis-ci.org/) for our CI/CD needs, so when you open a Pull Request you will trigger a build in Travis CI, which needs to pass before we consider merging the PR. We use Travis CI to test the following:
    * Create and load a MySQL database with the inventory static data.
    * Building and running the Inventory app against the MySQL database and run API tests.
    * Build and Deploy a Docker Container, using the same MySQL database.
    * Run API tests against the Docker Container.
    * Deploy a minikube cluster to test Helm charts.
    * Download Helm Chart dependencies and package the Helm chart.
    * Deploy the Helm Chart into Minikube.
    * Run API tests against the Helm Chart.

2. We use the Community Chart for MySQL as the dependency chart for the Inventory Chart. If you would like to learn more about that chart and submit issues/PRs, please check out its repo here:
    * https://github.com/helm/charts/tree/master/stable/mysql

### Contributing a New Chart Package to Microservices Reference Architecture Helm Repository
To contribute a new chart version to the [Microservices Reference Architecture](https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes/tree/spring) helm repository, follow its guide here:
* https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes/tree/spring#contributing-a-new-chart-to-the-helm-repositories
