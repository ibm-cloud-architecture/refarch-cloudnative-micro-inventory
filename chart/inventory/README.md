# refarch-cloudnative-micro-inventory: Spring Boot Microservice with MySQL Database

## Introduction
This chart will deploy a Spring Boot Application with a MySQL database onto a Kubernetes Cluster.

![Application Architecture](https://raw.githubusercontent.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/spring/static/inventory.png?raw=true)

Here is an overview of the chart's features:
- Leverage [`Spring Boot`](https://projects.spring.io/spring-boot/) framework to build a Microservices application.
- Uses [`Spring Data JPA`](http://projects.spring.io/spring-data-jpa/) to persist data to MySQL database.
- Uses [`MySQL`](https://www.mysql.com/) as the inventory database.
- Uses [`Docker`](https://docs.docker.com/) to package application binary and its dependencies.
- Uses [`Helm`](https://helm.sh/) to package application and MySQL deployment configuration and deploy to a [`Kubernetes`](https://kubernetes.io/) cluster.

## Chart Source
The source for the `Inventory` chart can be found at:
* https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/tree/master/spring/inventory

The source for the `MySQL` chart can be found at:
* https://github.com/helm/charts/tree/master/stable/mysql

## APIs
* Get all items in inventory:
    + `http://${WORKER_NODE_IP}:${NODE_PORT}/micro/inventory`

## Deploy Inventory Application to Kubernetes Cluster from CLI
To deploy the Inventory Chart and its MySQL dependency Chart to a Kubernetes cluster using Helm CLI, follow the instructions below:
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

# Clone inventory repository:
git clone -b spring --single-branch https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory.git

# Go to Chart Directory
cd refarch-cloudnative-micro-inventory/chart/inventory

# Deploy Inventory to Kubernetes cluster
helm upgrade --install inventory --set service.type=NodePort .
```