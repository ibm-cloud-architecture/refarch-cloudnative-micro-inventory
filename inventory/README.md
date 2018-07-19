# Cloudnative MicroProfile Microservice Integration with MySQL Database Server 

## Inventory Service - MicroProfile

This repository contains the **MicroProfile** implementation of the **Inventory Service** which is a part of 'IBM Cloud Native Reference Architecture' suite, available [here](https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes/tree/microprofile)

<p align="center">
  <a href="https://microprofile.io/">
    <img src="https://github.com/ibm-cloud-architecture/refarch-cloudnative-wfd/blob/microprofile/static/imgs/microprofile_small.png" width="300" height="100">
  </a>
</p>

## Table of Contents

* [Introduction](#introduction)
* [How it works](#how-it-works)
* [Implementation](#implementation)
    + [Microprofile](#microprofile)
* [Features](#features)
* [Deploying the App](#deploying-the-app)
    + [IBM Cloud Private](#ibm-cloud-private)
    + [Minikube](#minikube)
* [Run Inventory Service locally](#run-inventory-service-locally)
    + [Building the app](#building-the-app)
    + [Setting up MySQL](#setting-up-mysql)
    + [Setting up Zipkin](#setting-up-zipkin) (Optional)
    + [Running the app and stopping it](#running-the-app-and-stopping-it)
* [References](#references)

## Introduction

This project demonstrates the implementation of Inventory Microservice. The inventory microservice uses MySQL database as its datasource. It has the complete list of items of our store front. 

- Based on [MicroProfile](https://microprofile.io/).
- Uses MySQL as the inventory database.

## How it works

Inventory Microservice serves 'IBM Cloud Native Reference Architecture' suite, available [here](https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes/tree/microprofile), Microservice-based reference application. Though it is a part of a bigger application, Inventory service is itself an application in turn that manages the data from data store. Catalog Microservice serves as the cache to the Inventory.

## Implementation

### [MicroProfile](https://microprofile.io/)

MicroProfile is an open platform that optimizes the Enterprise Java for microservices architecture. In this application, we are using [**MicroProfile 1.3**](https://github.com/eclipse/microprofile-bom). This includes

- MicroProfile 1.0 ([JAX-RS 2.0](https://jcp.org/en/jsr/detail?id=339), [CDI 1.2](https://jcp.org/en/jsr/detail?id=346), and [JSON-P 1.0](https://jcp.org/en/jsr/detail?id=353))
- MicroProfile 1.1 
- MicroProfile 1.2 ([MicroProfile Fault Tolerance 1.0](https://github.com/eclipse/microprofile-fault-tolerance), [MicroProfile Health Check 1.0](https://github.com/eclipse/microprofile-health), [MicroProfile JWT Authentication 1.0](https://github.com/eclipse/microprofile-jwt-auth)).
- [MicroProfile Config 1.2](https://github.com/eclipse/microprofile-config) (supercedes MicroProfile Config 1.1), [MicroProfile Metrics 1.1](https://github.com/eclipse/microprofile-metrics) (supercedes MicroProfile Metrics 1.0), [MicroProfile OpenAPI 1.0](https://github.com/eclipse/microprofile-open-api), [MicroProfile OpenTracing 1.0](https://github.com/eclipse/microprofile-opentracing), [MicroProfile Rest Client 1.0](https://github.com/eclipse/microprofile-rest-client).

You can make use of this feature by including this dependency in Maven.

```
<dependency>
<groupId>org.eclipse.microprofile</groupId>
<artifactId>microprofile</artifactId>
<version>1.3</version>
<type>pom</type>
<scope>provided</scope>
</dependency>
```

You should also include a feature in [server.xml](https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/blob/microprofile/inventory/src/main/liberty/config/server.xml).

```
<server description="Sample Liberty server">

  <featureManager>
      <feature>microprofile-1.3</feature>
  </featureManager>

  <httpEndpoint httpPort="${default.http.port}" httpsPort="${default.https.port}"
      id="defaultHttpEndpoint" host="*" />

</server>
```
## Features

1. Java SE 8 - Used Java Programming language

2. [CDI 1.2](https://jcp.org/en/jsr/detail?id=346) - Used CDI for typesafe dependency injection

3. [JAX-RS 2.0](https://jcp.org/en/jsr/detail?id=339) - JAX-RS is used for providing both standard client and server APIs for RESTful communication by MicroProfile applications.

4. [Eclipse MicroProfile Config](https://github.com/eclipse/microprofile-config) - Configuration data comes from different sources like system properties, system environment variables, `.properties` file etc. These values may change dynamically. Using this feature helps us to pick up configured values immediately after they got changed.

The config values are sorted according to their ordinal. We can override the lower importance values from outside. The config sources by default, below is the order of importance.

- System.getProperties()
- System.getenv()
- all META-INF/microprofile-config.properties files on the ClassPath.

In our sample application, we obtained the configuration programatically.

5. [MicroProfile Health Check](https://github.com/eclipse/microprofile-health) - This feature helps us to determine the status of the service as well as its availability. This helps us to know if the service is healthy. If not, we can know the reasons behind the termination or shutdown. 

In our sample application, we injected this `/health` endpoint in our liveness probes.

6. [MicroProfile OpenAPI](https://github.com/eclipse/microprofile-open-api) - This feature helps us to expose the API documentation for the RESTful services. It allows the developers to produce OpenAPI v3 documents for their JAX-RS applications.

In our sample application, we used @OpenAPIDefinition, @Info, @Contact, @License, @APIResponses, @APIResponse, @Content, @Schema and @Operation annotations.

7. [MicroProfile Metrics](https://github.com/eclipse/microprofile-metrics) - This feature allows us to expose telemetry data. Using this, developers can monitor their services with the help of metrics.

In our sample application, we used @Timed, @Counted and @Metered annotations. These metrics are reused using `reuse` functionality. We also integrated them with Prometheus.

8. [MicroProfile OpenTracing](https://github.com/eclipse/microprofile-opentracing) - This feature enables distributed tracing. It helps us to analyze the transcation flow so that the we can easily debug the problematic services and fix them. It enables and allows for custom tracing of JAX-RS and non-JAX-RS methods. 

In our sample application, we used [Zipkin](https://zipkin.io/) as our distributed tracing system. We used @Traced and an ActiveSpan object to retrieve messages.

## Deploying the App

To build and run the entire BlueCompute demo application, each MicroService must be spun up together. This is due to how we
set up our Helm charts structure and how we dynamically produce our endpoints and URLs.  

Further instructions are provided [here](https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes/tree/microprofile).

### IBM Cloud Private

To deploy it on IBM Cloud Private, please follow the instructions provided [here](https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes/tree/microprofile#remotely-on-ibm-cloud-private).

### Minikube

To deploy it on Minikube, please follow the instructions provided [here](https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes/tree/microprofile#locally-in-minikube).

## Run Inventory Service locally

### Building the app

To build the application, we used maven build. Maven is a project management tool that is based on the Project Object Model (POM). Typically, people use Maven for project builds, dependencies, and documentation. Maven simplifies the project build. In this task, you use Maven to build the project.

1. Clone this repository.

   `git clone https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory.git`
   
   `cd refarch-cloudnative-micro-inventory/`

2. Checkout MicroProfile branch.

   `git checkout microprofile`

3. `cd inventory`

4. Run this command. This command builds the project and installs it.

   `mvn install`
   
   If this runs successfully, you will be able to see the below messages.

```
[INFO] --- maven-failsafe-plugin:2.18.1:verify (verify-results) @ inventory ---
[INFO] Failsafe report directory: /Users/user@ibm.com/BlueCompute/refarch-cloudnative-micro-inventory/inventory/target/test-reports/it
[INFO]
[INFO] --- maven-install-plugin:2.4:install (default-install) @ inventory ---
[INFO] Installing /Users/user@ibm.com/BlueCompute/refarch-cloudnative-micro-inventory/inventory/target/inventory-1.0-SNAPSHOT.war to /Users/user@ibm.com/.m2/repository/projects/inventory/1.0-SNAPSHOT/inventory-1.0-SNAPSHOT.war
[INFO] Installing /Users/user@ibm.com/BlueCompute/refarch-cloudnative-micro-inventory/inventory/pom.xml to /Users/user@ibm.com/.m2/repository/projects/inventory/1.0-SNAPSHOT/inventory-1.0-SNAPSHOT.pom
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 22.516 s
[INFO] Finished at: 2018-07-18T14:24:10-05:00
[INFO] Final Memory: 32M/467M
[INFO] ------------------------------------------------------------------------
```
### Setting up MySQL

To set up MySQL locally, we are running it as a docker container. You need [Docker](https://www.docker.com/) as a prerequisite.

To run MySQL on docker locally, run the below commands.

```
cd ..
cd mysql
```

1. Build the docker image.

`docker build -t mysql .`

2. Run the container.

`docker run -p 9041:3306 -d --name mysql -e MYSQL_ROOT_PASSWORD=password mysql`

3. It creates `items` table and loads the sample data.

```
cd ..
cd inventory
```

In this case, your jdbcURL will be 

```
export jdbcURL=jdbc:mysql://localhost:9041/inventorydb?useSSL=false
export dbuser=root
export dbpassword=password
```

### Setting up Zipkin 

This is an optional step.

In our sample application, we used Zipkin as our distributed tracing system.

If you want to access the traces for inventory service, run Zipkin as a docker container locally. You can find the instructions and more details [here](https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes/blob/microprofile/Zipkin/README.md)


### Running the app and stopping it

1. Set the JDBC URL before you start your application. The host and port depends on the service you use. You can run the MySQL server locally on your system using the MySQL docker container or use the [MySQL Compose](https://www.ibm.com/cloud/compose/mysql) available in [IBM Cloud](https://www.ibm.com/cloud/).

```
export jdbcURL=jdbc:mysql://<Your host>:<Port>/inventorydb?useSSL=false
export dbuser=<DB_USER_NAME>
export dbpassword=<PASSWORD>
```
Also set the Zipkin host and port to defaults.

```
export zipkinHost=localhost
export zipkinPort=9411
```

2. Start your server.

`mvn liberty:start-server -DtestServerHttpPort=9081`

You will see something similar to the below messages.

```
[INFO] Starting server defaultServer.
[INFO] Server defaultServer started with process ID 13980.
[INFO] Waiting up to 30 seconds for server confirmation:  CWWKF0011I to be found in /Users/user@ibm.com/BlueCompute/refarch-cloudnative-micro-inventory/inventory/target/liberty/wlp/usr/servers/defaultServer/logs/messages.log
[INFO] CWWKM2010I: Searching for CWWKF0011I in /Users/user@ibm.com/BlueCompute/refarch-cloudnative-micro-inventory/inventory/target/liberty/wlp/usr/servers/defaultServer/logs/messages.log. This search will timeout after 30 seconds.
[INFO] CWWKM2015I: Match number: 1 is [18/7/18 14:42:32:751 CDT] 0000001a com.ibm.ws.kernel.feature.internal.FeatureManager            A CWWKF0011I: The server defaultServer is ready to run a smarter planet..
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 9.889 s
[INFO] Finished at: 2018-07-18T14:42:32-05:00
[INFO] Final Memory: 14M/309M
[INFO] ------------------------------------------------------------------------
```

3. Validate the inventory service. You should get a list of all inventory items.
```
curl http://localhost:9081/inventory/rest/inventory
```

4. If you are done accessing the application, you can stop your server using the following command.

`mvn liberty:stop-server -DtestServerHttpPort=9081`

Once you do this, you see the below messages.

```
[INFO] CWWKM2001I: Invoke command is [/Users/user@ibm.com/BlueCompute/refarch-cloudnative-micro-inventory/inventory/target/liberty/wlp/bin/server, stop, defaultServer].
[INFO] objc[14896]: Class JavaLaunchHelper is implemented in both /Library/Java/JavaVirtualMachines/jdk1.8.0_151.jdk/Contents/Home/jre/bin/java (0x101bdd4c0) and /Library/Java/JavaVirtualMachines/jdk1.8.0_151.jdk/Contents/Home/jre/lib/libinstrument.dylib (0x101c9b4e0). One of the two will be used. Which one is undefined.
[INFO] Stopping server defaultServer.
[INFO] Server defaultServer stopped.
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 3.146 s
[INFO] Finished at: 2018-07-18T15:59:32-05:00
[INFO] Final Memory: 13M/245M
[INFO] ------------------------------------------------------------------------
```

### References

1. [Microprofile](https://microprofile.io/)
2. [MicroProfile Config on Liberty](https://www.ibm.com/support/knowledgecenter/en/SSAW57_liberty/com.ibm.websphere.wlp.nd.multiplatform.doc/ae/twlp_microprofile_appconfig.html)
3. [MicroProfile Health Checks on Liberty](https://www.ibm.com/support/knowledgecenter/en/SSEQTP_liberty/com.ibm.websphere.wlp.doc/ae/twlp_microprofile_healthcheck.html)
4. [MicroProfile OpenAPI on Liberty](https://www.ibm.com/support/knowledgecenter/en/SSEQTP_liberty/com.ibm.websphere.wlp.doc/ae/twlp_mpopenapi.html)
5. [MicroProfile Metrics on Liberty](https://www.ibm.com/support/knowledgecenter/en/SSEQTP_liberty/com.ibm.websphere.wlp.doc/ae/cwlp_mp_metrics_api.html)
6. [MicroProfile OpenTracing on Liberty](https://www.ibm.com/support/knowledgecenter/SSEQTP_liberty/com.ibm.websphere.liberty.autogen.base.doc/ae/rwlp_feature_mpOpenTracing-1.0.html)

