# Cloudnative MicroProfile Microservice Integration with Elasticsearch

## Catalog Service - MicroProfile

This repository contains the **MicroProfile** implementation of the **Catalog Service** which is a part of 'IBM Cloud Native Reference Architecture' suite, available [here](https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes/tree/microprofile).

<p align="center">
  <a href="https://microprofile.io/">
    <img src="https://github.com/ibm-cloud-architecture/refarch-cloudnative-wfd/blob/microprofile/static/imgs/microprofile_small.png" width="300" height="100">
  </a>
</p>

## Table of Contents

* [Introduction](#introduction)
* [How it works](#how-it-works)
* [API Endpoints](#api-endpoints)
* [Implementation](#implementation)
    + [Microprofile](#microprofile)
* [Features](#features)
* [Deploying the App](#deploying-the-app)
    + [IBM Cloud Private](#ibm-cloud-private)
    + [Minikube](#minikube)
* [Run Catalog Service locally](#run-catalog-service-locally)
    + [Building the app](#building-the-app)
    + [Setting up Elasticsearch](#setting-up-elasticsearch)
    + [Setting up Zipkin](#setting-up-zipkin)
    + [Running the app and stopping it](#running-the-app-and-stopping-it)
* [References](#references)

## Introduction

This project demonstrates the implementation of Catalog Microservice. The catalog microservice uses Elastic Search and serves as cache to the Inventory service. 

- Based on [MicroProfile](https://microprofile.io/).
- Uses Elasticsearch.
- Deployment options for Minikube environment and ICP.

## How it works

Catalog Microservice serves 'IBM Cloud Native Reference Architecture' suite, available at https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes, Microservice-based reference application. Though it is a part of a bigger application, Catalog service is itself an application in turn that serves as cache to the Inventory Service.

## API Endpoints

```
GET     /catalog/rest/items/              # Returns all items in inventory
GET     /catalog/rest/items/{id}          # Returns item by id 
```
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

You should also include a feature in [server.xml](https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/blob/microprofile/catalog/src/main/liberty/config/server.xml).

```
<server description="Sample Liberty server">

  <featureManager>
      <feature>microprofile-1.3</feature>
  </featureManager>

  <httpEndpoint httpPort="${default.http.port}" httpsPort="${default.https.port}"
      id="defaultHttpEndpoint" host="*" />

</server>
```
### Features

1. Java SE 8 - Used Java Programming language

2. [CDI 1.2](https://jcp.org/en/jsr/detail?id=346) - Used CDI for typesafe dependency injection

3. [JAX-RS 2.0](https://jcp.org/en/jsr/detail?id=339) - JAX-RS is used for providing both standard client and server APIs for RESTful communication by MicroProfile applications.

4. [Eclipse MicroProfile Config](https://github.com/eclipse/microprofile-config) - Configuration data comes from different sources like system properties, system environment variables, `.properties` file etc. These values may change dynamically. Using this feature helps us to pick up configured values immediately after they got changed.

The config values are sorted according to their ordinal. We can override the lower importance values from outside. The config sources by default, below is the order of importance.

- System.getProperties()
- System.getenv()
- all META-INF/microprofile-config.properties files on the ClassPath.

In our sample application, we obtained the configuration programatically.

5. [Microprofile Fault Tolerance](https://github.com/eclipse/microprofile-fault-tolerance) - This feature helps us to build faulttolerance microservices. In some situations, there may be some impact on the system and it may fail due to several reasons. To avoid such failures, we can design fault tolerant microservices using this feature.

In our sample application, we used @Timeout, @Retry and @Fallback.

6. [MicroProfile Health Check](https://github.com/eclipse/microprofile-health) - This feature helps us to determine the status of the service as well as its availability. This helps us to know if the service is healthy. If not, we can know the reasons behind the termination or shutdown. 

In our sample application, we injected this `/health` endpoint in our liveness probes.

7. [MicroProfile Rest Client](https://github.com/eclipse/microprofile-rest-client) - This feature helps us to define typesafe rest clients. These are defined as Java interfaces. The available RESTful apis in our sample application are invoked in a type safe manner.

8. [MicroProfile OpenAPI](https://github.com/eclipse/microprofile-open-api) - This feature helps us to expose the API documentation for the RESTful services. It allows the developers to produce OpenAPI v3 documents for their JAX-RS applications.

In our sample application we used @OpenAPIDefinition, @Info, @Contact, @License, @APIResponses, @APIResponse, @Content, @Schema, @Operation and @Parameter annotations.

9. [MicroProfile Metrics](https://github.com/eclipse/microprofile-metrics) - This feature allows us to expose telemetry data. Using this, developers can monitor their services with the help of metrics.

In our sample application, we used @Timed, @Counted and @Metered annotations. These metrics are reused using reuse functionality. We also integrated Microprofile metrics with Prometheus.

10. [MicroProfile OpenTracing](https://github.com/eclipse/microprofile-opentracing) - This feature enables distributed tracing. It helps us to analyze the transcation flow so that the we can easily debug the problematic services and fix them. It enables and allows for custom tracing of JAX-RS and non-JAX-RS methods. 

In our sample application, we used [Zipkin](https://zipkin.io/) as our distributed tracing system. We used @Traced and an ActiveSpan object to retrieve messages.

## Deploying the App

To build and run the entire BlueCompute demo application, each MicroService must be spun up together. This is due to how we
set up our Helm charts structure and how we dynamically produce our endpoints and URLs.  

Further instructions are provided [here](https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes/tree/microprofile).

### IBM Cloud Private

To deploy it on IBM Cloud Private, please follow the instructions provided [here](https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes/tree/microprofile#remotely-on-ibm-cloud-private).

### Minikube

To deploy it on Minikube, please follow the instructions provided [here](https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes/tree/microprofile#locally-in-minikube).

## Run Catalog Service locally

### Building the app

To build the application, we used maven build. Maven is a project management tool that is based on the Project Object Model (POM). Typically, people use Maven for project builds, dependencies, and documentation. Maven simplifies the project build. In this task, you use Maven to build the project.

1. Clone this repository.

   `git clone https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory.git`
   
2. `cd refarch-cloudnative-micro-inventory/`

3. Checkout MicroProfile branch.

   `git checkout microprofile`
   
4. Go to Catalog ... `cd catalog`

5. Run this command. This command builds the project and installs it.

   `mvn install`
   
   If this runs successfully, you will be able to see the below messages.
   
```
[INFO] --- maven-failsafe-plugin:2.18.1:verify (verify-results) @ catalog ---
[INFO] Failsafe report directory: /Users/user@ibm.com/BlueCompute/refarch-cloudnative-micro-inventory/catalog/target/test-reports/it
[INFO]
[INFO] --- maven-install-plugin:2.4:install (default-install) @ catalog ---
[INFO] Installing /Users/user@ibm.com/BlueCompute/refarch-cloudnative-micro-inventory/catalog/target/catalog-1.0-SNAPSHOT.war to /Users/user@ibm.com/.m2/repository/projects/catalog/1.0-SNAPSHOT/catalog-1.0-SNAPSHOT.war
[INFO] Installing /Users/user@ibm.com/BlueCompute/refarch-cloudnative-micro-inventory/catalog/pom.xml to /Users/user@ibm.com/.m2/repository/projects/catalog/1.0-SNAPSHOT/catalog-1.0-SNAPSHOT.pom
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 14.150 s
[INFO] Finished at: 2018-07-19T11:29:12-05:00
[INFO] Final Memory: 24M/309M
[INFO] ------------------------------------------------------------------------
```
### Setting up Elasticsearch

To set up Elasticsearch locally, we are running it as a docker container. You need [Docker](https://www.docker.com/) as a prerequisite.

To run Elasticsearch on docker locally, run the below commands.

1. Build the docker image

`docker pull ibmcase/bluecompute-elasticsearch`

2. Run the container.

`docker run -d -p 9200:9200 --name elasticsearch ibmcase/bluecompute-elasticsearch`

In this case, your environment variables will be 

```
export elasticsearch_url=http://localhost:9200
```

### Setting up Zipkin

This is an optional step.

In our sample application, we used Zipkin as our distributed tracing system.

If you want to access the traces for catalog service, run Zipkin as a docker container locally. You can find the instructions and more details [here](https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes/blob/microprofile/Zipkin/README.md)

### Running the app and stopping it

1. Set the environment variables before you start your application. The host and port depends on the service you use. You can run the Elastic Search locally on your system using the Elastic Search docker container or use the [Elasticsearch Compose](https://www.ibm.com/cloud/compose/elasticsearch) available in [IBM Cloud](https://www.ibm.com/cloud/).

```
export elasticsearch_url=http://<Your host>:<Port>
```
Set the required Inventory URLs.

```
export inventory_health=http://localhost:9081/inventory/health
```

Also set the Zipkin host and port to defaults.

```
export zipkinHost=localhost
export zipkinPort=9411
```

Also, we need to set the `client.InventoryServiceClient/mp-rest/url` to `http://localhost:9081/inventory/rest/inventory/` which allows the rest client to invoke Inventory in a type safe manner.

To set this, do the following.

Insert `-Dclient.InventoryServiceClient/mp-rest/url=http://localhost:9081/inventory/rest/inventory/` in the [jvm.options](https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/blob/microprofile/catalog/src/main/liberty/config/jvm.options) located at `src/main/liberty/config`

After doing run, run `mvn install`

2. Start your server.

`mvn liberty:start-server -DtestServerHttpPort=9082`

You will see something similar to the below messages.

```
[INFO] Starting server defaultServer.
[INFO] Server defaultServer started with process ID 18769.
[INFO] Waiting up to 30 seconds for server confirmation:  CWWKF0011I to be found in /Users/user@ibm.com/BlueCompute/refarch-cloudnative-micro-inventory/catalog/target/liberty/wlp/usr/servers/defaultServer/logs/messages.log
[INFO] CWWKM2010I: Searching for CWWKF0011I in /Users/user@ibm.com/BlueCompute/refarch-cloudnative-micro-inventory/catalog/target/liberty/wlp/usr/servers/defaultServer/logs/messages.log. This search will timeout after 30 seconds.
[INFO] CWWKM2015I: Match number: 1 is [19/7/18 11:37:58:362 CDT] 0000001a com.ibm.ws.kernel.feature.internal.FeatureManager            A CWWKF0011I: The server defaultServer is ready to run a smarter planet..
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 9.056 s
[INFO] Finished at: 2018-07-19T11:37:58-05:00
[INFO] Final Memory: 14M/309M
[INFO] ------------------------------------------------------------------------
```

3. Validate the catalog service in the following way. You should see a list of inventory items.

```
curl -X GET http://localhost:9082/catalog/rest/items
```

4. If you are done accessing the application, you can stop your server using the following command.

`mvn liberty:stop-server -DtestServerHttpPort=9082`

Once you do this, you see the below messages.

```
[INFO] CWWKM2001I: Invoke command is [/Users/user@ibm.com/BlueCompute/refarch-cloudnative-micro-inventory/catalog/target/liberty/wlp/bin/server, stop, defaultServer].
[INFO] objc[18818]: Class JavaLaunchHelper is implemented in both /Library/Java/JavaVirtualMachines/jdk1.8.0_151.jdk/Contents/Home/jre/bin/java (0x10e9724c0) and /Library/Java/JavaVirtualMachines/jdk1.8.0_151.jdk/Contents/Home/jre/lib/libinstrument.dylib (0x1109f54e0). One of the two will be used. Which one is undefined.
[INFO] Stopping server defaultServer.
[INFO] Server defaultServer stopped.
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 2.372 s
[INFO] Finished at: 2018-07-19T11:40:37-05:00
[INFO] Final Memory: 14M/309M
[INFO] ------------------------------------------------------------------------
```


### References

1. [Microprofile](https://microprofile.io/)
2. [MicroProfile Config on Liberty](https://www.ibm.com/support/knowledgecenter/en/SSAW57_liberty/com.ibm.websphere.wlp.nd.multiplatform.doc/ae/twlp_microprofile_appconfig.html)
3. [Microprofile Fault Tolerance on Liberty](https://www.ibm.com/support/knowledgecenter/en/was_beta_liberty/com.ibm.websphere.wlp.nd.multiplatform.doc/ae/twlp_microprofile_fault_tolerance.html)
4. [MicroProfile Health Checks on Liberty](https://www.ibm.com/support/knowledgecenter/en/SSEQTP_liberty/com.ibm.websphere.wlp.doc/ae/twlp_microprofile_healthcheck.html)
5. [MicroProfile Rest Client on Liberty](https://www.ibm.com/support/knowledgecenter/en/was_beta_liberty/com.ibm.websphere.wlp.nd.multiplatform.doc/ae/twlp_mp_restclient.html)
4. [MicroProfile OpenAPI on Liberty](https://www.ibm.com/support/knowledgecenter/en/SSEQTP_liberty/com.ibm.websphere.wlp.doc/ae/twlp_mpopenapi.html)
5. [MicroProfile Metrics on Liberty](https://www.ibm.com/support/knowledgecenter/en/SSEQTP_liberty/com.ibm.websphere.wlp.doc/ae/cwlp_mp_metrics_api.html)
6. [MicroProfile OpenTracing on Liberty](https://www.ibm.com/support/knowledgecenter/SSEQTP_liberty/com.ibm.websphere.liberty.autogen.base.doc/ae/rwlp_feature_mpOpenTracing-1.0.html)



