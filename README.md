###### refarch-cloudnative-micro-inventory

## Microprofile based Microservice Apps Integration with ElasticSearch and MySQL Database Server

This repository contains the **MicroProfile** implementation of the **Inventory Service** and **Catalog Service** which are a part of the 'IBM Cloud Native Reference Architecture' suite, available at https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes

<p align="center">
  <a href="https://microprofile.io/">
    <img src="https://github.com/ibm-cloud-architecture/refarch-cloudnative-wfd/blob/microprofile/static/imgs/microprofile_small.png" width="300" height="100">
  </a>
</p>

1. [Introduction](#introduction)
2. [How it works](#how-it-works)
3. [API Endpoints](#api-endpoints)
4. [Implementation](#implementation)
    1. [Liberty app accelerator](#liberty-app-accelerator)
    2. [Microprofile](#microprofile)
5. [Features and App details](#features)
6. [Building the app](#building-the-app)
7. [Running the app and stopping it](#running-the-app-and-stopping-it)
    1. [Pre-requisites](#pre-requisites)
    2. [Locally in JVM](#locally-in-jvm)
    3. [Locally in Containers](#locally-in-containers)
    4. [Locally in Minikube](#locally-in-minikube)
    5. [Remotely in ICP](#remotely-in-icp)
8. [DevOps Strategy](#devops-strategy)
9. [References](#references)

### Introduction

This project is built to demonstrate how to build two Microservices applications using Microprofile. The first application [Inventory](https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/tree/microprofile/inventory) uses MySQL database as its datasource. The second application [Catalog](https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/tree/microprofile/catalog) serves as a cache to [Inventory](https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/tree/microprofile/inventory) by leveraging Elasticsearch as its datasource.

- Based on [MicroProfile](https://microprofile.io/).
- Uses MySQL as the [Inventory](https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/tree/microprofile/inventory) database.
- Elasticsearch is used as the [Catalog](https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/tree/microprofile/catalog) microservice's data source.
- Devops - TBD
- Deployment options for local, Docker Container-based runtimes, Minikube environment and ICP/BMX.

### How it works

Inventory and Catalog Microservices serves 'IBM Cloud Native Reference Architecture' suite, available at
https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes. Though it is a part of a bigger application, Inventory and Catalog services are themselves individual applications.

<p align="center">
    <img src="https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes/blob/microprofile/static/imgs/inventory-catalog.png">
</p>

### API Endpoints

```
GET     /catalog/rest/items/              # Returns all items in inventory
GET     /catalog/rest/items/{id}          # Returns item by id 
```
You can use cURL or Chrome POSTMAN to send get/post/put/delete requests to the application.

### Implementation

#### [Liberty app accelerator](https://liberty-app-accelerator.wasdev.developer.ibm.com/start/)

For Liberty, there is nice tool called [Liberty Accelerator](https://liberty-app-accelerator.wasdev.developer.ibm.com/start/) that generates a simple project based upon your configuration. Using this, you can build and deploy to Liberty either using the Maven or Gradle build.

<p align="center">
    <img src="https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes/blob/microprofile/static/imgs/LibertyAcc_Home.png">
</p>

Just check the options of your choice and click Generate project. You can either Download it as a zip or you can create git project.

<p align="center">
    <img src="https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes/blob/microprofile/static/imgs/LibertyAcc_PrjGen.png">
</p>

Once you are done with this, you will have a sample microprofile based application that you can deploy on Liberty.

Using Liberty Accelerator is your choice. You can also create the entire project manually, but using Liberty Accelerator will make things easier.

#### [MicroProfile](https://microprofile.io/)

MicroProfile is an open platform that optimizes the Enterprise Java for microservices architecture. In this application, we are using [**MicroProfile 1.2**](https://github.com/eclipse/microprofile-bom). This includes

- MicroProfile 1.0 ([JAX-RS 2.0](https://jcp.org/en/jsr/detail?id=339), [CDI 1.2](https://jcp.org/en/jsr/detail?id=346), and [JSON-P 1.0](https://jcp.org/en/jsr/detail?id=353))
- MicroProfile 1.1 (MicroProfile 1.0, [MicroProfile Config 1.0.](https://github.com/eclipse/microprofile-config))
- [MicroProfile Config 1.1](https://github.com/eclipse/microprofile-config) (supercedes MicroProfile Config 1.0), [MicroProfile Fault Tolerance 1.0](https://github.com/eclipse/microprofile-fault-tolerance), [MicroProfile Health Check 1.0](https://github.com/eclipse/microprofile-health), [MicroProfile Metrics 1.0](https://github.com/eclipse/microprofile-metrics), [MicroProfile JWT Authentication 1.0](https://github.com/eclipse/microprofile-jwt-auth).

You can make use of this feature by including this dependency in Maven.

```
<dependency>
<groupId>org.eclipse.microprofile</groupId>
<artifactId>microprofile</artifactId>
<version>1.2</version>
<type>pom</type>
<scope>provided</scope>
</dependency>
```

You should also include a feature in **server.xml**.

```
<server description="Sample Liberty server">

  <featureManager>
      <feature>microprofile-1.2</feature>
  </featureManager>

  <httpEndpoint httpPort="${default.http.port}" httpsPort="${default.https.port}"
      id="defaultHttpEndpoint" host="*" />

</server>
```
### Features

1. Java SE 8 - Used Java Programming language

2. CDI 1.2 - Used CDI for typesafe dependency injection

3. JAX-RS 2.0.1 - JAX-RS is used for providing both standard client and server APIs for RESTful communication by MicroProfile applications.

4. Eclipse MicroProfile Config 1.1 - Configuration data comes from different sources like system properties, system environment variables, .properties etc. These values may change dynamically. Using this feature, helps us to pick up configured values immediately after they got changed.

The config values are sorted according to their ordinal. We can override the lower importance values from outside. The config sources by default, below is the order of importance.

- System.getProperties()
- System.getenv()
- all META-INF/microprofile-config.properties files on the ClassPath.

In our sample application, we obtained the configuration programatically.

### Building the app

To build the application, we used maven build. Maven is a project management tool that is based on the Project Object Model (POM). Typically, people use Maven for project builds, dependencies, and documentation. Maven simplifies the project build. In this task, you use Maven to build the project.

1. Clone this repository.

   `git clone https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory.git`

2. Checkout MicroProfile branch.

   `git checkout microprofile`

3. To build the [Inventory](https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/tree/microprofile/inventory) microservice, click [here](https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/tree/microprofile/inventory#building-the-app) to access the instructions.

4. To build the [Catalog](https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/tree/microprofile/catalog) microservice, click [here](https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/tree/microprofile/catalog#building-the-app) to access the instructions.

### Running the app and stopping it

### Pre-requisites

1. Locally in JVM

Pre-requisites for the [Inventory](https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/tree/microprofile/inventory) microservice can be accessed [here](https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/tree/microprofile/inventory#pre-requisites).

Pre-requisites for the [Catalog](https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/tree/microprofile/catalog) microservice can be accessed [here](https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/tree/microprofile/catalog#pre-requisites).

### Locally in JVM

To run [Inventory](https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/tree/microprofile/inventory) application locally on JVM, access the instructions [here](https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/tree/microprofile/inventory#locally-in-jvm).

To run [Catalog](https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/tree/microprofile/catalog) application locally on JVM, access the instructions [here](https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/tree/microprofile/catalog#locally-in-jvm).

