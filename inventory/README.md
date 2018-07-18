# Cloudnative MicroProfile Microservice Integration with MySQL Database Server 

## Inventory Service - MicroProfile

This repository contains the **MicroProfile** implementation of the **Inventory Service** which is a part of 'IBM Cloud Native Reference Architecture' suite, available at https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes

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
* [App details](#app-details)
* [Deploying the App](#deploying-the-app)
    + [Minikube](#minikube)
    + [IBM Cloud Private](#ibm-cloud-private)
* [Run Inventory Service locally](#run-inventory-service-locally)
    + [Building the app](#building-the-app)
    + [Setting up MYSQL](#setting-up-mysql)
    + [Running the app and stopping it](#running-the-app-and-stopping-it)
* [References](#references)

## Introduction

This project demonstrates the implementation of Inventory Microservice. The inventory microservice uses MySQL database as its datasource. It has the complete list of items of our store front. 

- Based on [MicroProfile](https://microprofile.io/).
- Uses MySQL as the inventory database.

## How it works

Inventory Microservice serves 'IBM Cloud Native Reference Architecture' suite, available at https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes, Microservice-based reference application. Though it is a part of a bigger application, Inventory service is itself an application in turn that manages the data from data store. Catalog Microservice serves as the cache to the Inventory.

## Implementation

### [MicroProfile](https://microprofile.io/)

MicroProfile is an open platform that optimizes the Enterprise Java for microservices architecture. In this application, we are using [**MicroProfile 1.3**](https://github.com/eclipse/microprofile-bom). This includes

- MicroProfile 1.0 ([JAX-RS 2.0](https://jcp.org/en/jsr/detail?id=339), [CDI 1.2](https://jcp.org/en/jsr/detail?id=346), and [JSON-P 1.0](https://jcp.org/en/jsr/detail?id=353))
- MicroProfile 1.1 (MicroProfile 1.0, [MicroProfile Config 1.0.](https://github.com/eclipse/microprofile-config))
- MicroProfile 1.2 ([MicroProfile Config 1.1](https://github.com/eclipse/microprofile-config) (supercedes MicroProfile Config 1.0), [MicroProfile Fault Tolerance 1.0](https://github.com/eclipse/microprofile-fault-tolerance), [MicroProfile Health Check 1.0](https://github.com/eclipse/microprofile-health), [MicroProfile Metrics 1.0](https://github.com/eclipse/microprofile-metrics), [MicroProfile JWT Authentication 1.0](https://github.com/eclipse/microprofile-jwt-auth)).
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

2. CDI 1.2 - Used CDI for typesafe dependency injection

3. JAX-RS 2.0.1 - JAX-RS is used for providing both standard client and server APIs for RESTful communication by MicroProfile applications.

4. Eclipse MicroProfile Config - Configuration data comes from different sources like system properties, system environment variables, .properties etc. These values may change dynamically. Using this feature, helps us to pick up configured values immediately after they got changed.

The config values are sorted according to their ordinal. We can override the lower importance values from outside. The config sources by default, below is the order of importance.

- System.getProperties()
- System.getenv()
- all META-INF/microprofile-config.properties files on the ClassPath.

In our sample application, we obtained the configuration programatically.

5. MicroProfile Health Check - This feature helps us to determine the status of the service as well as its availability. This helps us to know if the service is healthy. If not, we can know the reasons behind the termination or shutdown. 

In our sample application, we injected this `/health` endpoint in our liveness probes.

6. MicroProfile OpenAPI - This feature helps us to expose the API documentation for the RESTful services. It allows the developers to produce OpenAPI v3 documents for their JAX-RS applications.

In our sample application, we used @OpenAPIDefinition, @Info, @Contact, @License, @APIResponses, @APIResponse, @Content, @Schema and @Operation annotations.

7. MicroProfile Metrics - This feature allows us to expose telemetry data. Using this, developers can monitor their services with the help of metrics.

In our sample application, we used @Timed, @Counted and @Metered annotations. These metrics are reused using `reuse` functionality. We also integrated them with Prometheus.

8. MicroProfile OpenTracing - This feature enables distributed tracing. It helps us to analyze the transcation flow so that the we can easily debug the problematic services and fix them.

In our sample application, we used [Zipkin](https://zipkin.io/) as our distributed tracing system.

## App details

TBD

## Deploying the App

TBD

### Minikube

TBD

### IBM Cloud Private

TBD

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
[INFO] Total time: 39.004 s
[INFO] Finished at: 2018-02-22T15:14:59-06:00
[INFO] Final Memory: 22M/307M
[INFO] ------------------------------------------------------------------------
```
### Setting up MYSQL

To set up MYSQL locally, we are running it as a docker container. You need [Docker](https://www.docker.com/) as a prerequisite.

To run MYSQL on docker locally, run the below commands.

```
cd ..
cd mysql
```

1. Build the docker image.

`docker build -t mysql .`

2. Run the container.

`docker run -p 9041:3306 -d --name mysql -e MYSQL_ROOT_PASSWORD=password mysql`

3. Create `items` table and load sample data.

`docker exec mysql ./load-data.sh root password 0.0.0.0 3306`

In this case, your jdbcURL will be 

```
export jdbcURL=jdbc:mysql://localhost:9041/inventorydb?useSSL=false
export dbuser=root
export dbpassword=password
```

### Running the app and stopping it

1. Set the JDBC URL before you start your application. The host and port depends on the service you use. You can run the MYSQL server locally on your system using the MYSQL docker container or use the [MYSQL Compose](https://www.ibm.com/cloud/compose/mysql) available in [IBM Cloud](https://www.ibm.com/cloud/).

```
export jdbcURL=jdbc:mysql://<Your host>:<Port>/inventorydb?useSSL=false
export dbuser=<DB_USER_NAME>
export dbpassword=<PASSWORD>
```

2. Start your server.

`mvn liberty:start-server -DtestServerHttpPort=9081`

You will see something similar to the below messages.

```
[INFO] Starting server defaultServer.
[INFO] Server defaultServer started with process ID 62300.
[INFO] Waiting up to 30 seconds for server confirmation:  CWWKF0011I to be found in /Users/user@ibm.com/BlueCompute/refarch-cloudnative-micro-inventory/inventory/target/liberty/wlp/usr/servers/defaultServer/logs/messages.log
[INFO] CWWKM2010I: Searching for CWWKF0011I in /Users/Hemankita.Perabathini@ibm.com/BlueCompute/refarch-cloudnative-micro-inventory/inventory/target/liberty/wlp/usr/servers/defaultServer/logs/messages.log. This search will timeout after 30 seconds.
[INFO] CWWKM2015I: Match number: 1 is [22/2/18 15:19:34:512 CST] 00000019 com.ibm.ws.kernel.feature.internal.FeatureManager            A CWWKF0011I: The server defaultServer is ready to run a smarter planet..
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 23.456 s
[INFO] Finished at: 2018-02-22T15:19:34-06:00
[INFO] Final Memory: 13M/309M
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
[INFO] CWWKM2001I: Invoke command is [/Users/Hemankita.Perabathini@ibm.com/BlueCompute/refarch-cloudnative-micro-inventory/inventory/target/liberty/wlp/bin/server, stop, defaultServer].
[INFO] objc[62340]: Class JavaLaunchHelper is implemented in both /Library/Java/JavaVirtualMachines/jdk1.8.0_131.jdk/Contents/Home/jre/bin/java (0x10db7e4c0) and /Library/Java/JavaVirtualMachines/jdk1.8.0_131.jdk/Contents/Home/jre/lib/libinstrument.dylib (0x10dc784e0). One of the two will be used. Which one is undefined.
[INFO] Stopping server defaultServer.
[INFO] Server defaultServer stopped.
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 2.088 s
[INFO] Finished at: 2018-02-22T15:20:44-06:00
[INFO] Final Memory: 12M/245M
[INFO] ------------------------------------------------------------------------
```

### References

1. [Microprofile](https://microprofile.io/)
