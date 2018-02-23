# Cloudnative MicroProfile Microservice Integration with Elasticsearch

## Inventory Service - MicroProfile

This repository contains the **MicroProfile** implementation of the **Catalog Service** which is a part of 'IBM Cloud Native Reference Architecture' suite, available at https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes

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

This project demonstrates the implementation of Catalog Microservice. The catalog microservice uses Elastic Search and serves as cache to the Inventory service. 

- Based on [MicroProfile](https://microprofile.io/).
- Uses Elasticsearch.
- Devops - TBD
- Deployment options for local, Docker Container-based runtimes, Minikube environment and ICP/BMX.

### How it works

Catalog Microservice serves 'IBM Cloud Native Reference Architecture' suite, available at https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes, Microservice-based reference application. Though it is a part of a bigger application, Catalog service is itself an application in turn that serves as cache to the Inventory Service.

### API Endpoints

```
GET     /catalog/rest/items/              # Returns all items in inventory
GET     /catalog/rest/items/{id}          # Returns item by id 
```
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

You should also include a feature in [server.xml](https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/blob/microprofile/catalog/src/main/liberty/config/server.xml).

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

3. `cd refarch-cloudnative-micro-inventory/`

   `cd catalog`

4. Run this command. This command builds the project and installs it.

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
[INFO] Total time: 31.104 s
[INFO] Finished at: 2018-02-23T10:13:57-06:00
[INFO] Final Memory: 21M/250M
[INFO] ------------------------------------------------------------------------
```
### Running the app and stopping it

### Pre-requisites

As Catalog service serves as cache to the Inventory service, make sure the inventory service is up and running before running the Catalog.

1. Locally in JVM

To run the Catalog microservice locally in JVM, please complete the [Building the app](#building-the-app) section.

**Set Up Elasticsearch on IBM Cloud**

1. [Provision](https://console.ng.bluemix.net/catalog/services/compose-for-elasticsearch) and instance of Elasticsearch into your Bluemix space.
    - Select name for your instance.
    - Click the `Create` button.
2. Refresh the page until you see `Status: Ready`.
3. Now obtain `Elasticsearch` service credentials.
    - Click on `Service Credentials` tab.
    - Then click on the `View Credentials` dropdown next to the credentials.
4. See the `uri` field, which has the format `https://user:password@host:port/`, and extract the following:
    - **user:** Elasticsearch user.
    - **password:** Elasticsearch password.
    - **host**: Elasticsearch host.
    - **port:** Elasticsearch port.
5. Keep those credential handy for when deploying the Inventory and Catalog services in the following sections.

Elasticsearch database is now setup in Compose.

In this case, your env variables will be

```
export elasticsearch_url=http://<Elasticsearch host>:<Elasticsearch port>
export inventory_url=http://localhost:9081/inventory/rest/inv/inventory/
export inventory_health=http://localhost:9081/inventory/rest/inv/check/
```

**Set Up Elasticsearch on Docker locally**

1. Build the docker image

`docker pull ibmcase/bluecompute-elasticsearch`

2. Run the container.

`docker run -d -p 9200:9200 --name elasticsearch ibmcase/bluecompute-elasticsearch`

In this case, your environment variables will be 

```
export elasticsearch_url=http://localhost:9200
export inventory_url=http://localhost:9081/inventory/rest/inv/inventory/
export inventory_health=http://localhost:9081/inventory/rest/inv/check/
```

### Locally in JVM

1. Set the environment variables before you start your application. The host and port depends on the service you use. You can run the Elastic Search locally on your system using the Elastic Search docker container or use the [Elasticsearch Compose](https://www.ibm.com/cloud/compose/elasticsearch) available in [IBM Cloud](https://www.ibm.com/cloud/).

   ```
   export elasticsearch_url=http://<Your host>:<Port>
   export inventory_url=http://localhost:9081/inventory/rest/inv/inventory/
   export inventory_health=http://localhost:9081/inventory/rest/inv/check/
   ```

2. Start your server.

   `mvn liberty:start-server -DtestServerHttpPort=9082`

   You will see the below.
```
[INFO] Starting server defaultServer.
[INFO] Server defaultServer started with process ID 68974.
[INFO] Waiting up to 30 seconds for server confirmation:  CWWKF0011I to be found in /Users/user@ibm.com/BlueCompute/refarch-cloudnative-micro-inventory/catalog/target/liberty/wlp/usr/servers/defaultServer/logs/messages.log
[INFO] CWWKM2010I: Searching for CWWKF0011I in /Users/user@ibm.com/BlueCompute/refarch-cloudnative-micro-inventory/catalog/target/liberty/wlp/usr/servers/defaultServer/logs/messages.log. This search will timeout after 30 seconds.
[INFO] CWWKM2015I: Match number: 1 is [23/2/18 12:43:03:391 CST] 00000019 com.ibm.ws.kernel.feature.internal.FeatureManager            A CWWKF0011I: The server defaultServer is ready to run a smarter planet..
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 18.987 s
[INFO] Finished at: 2018-02-23T12:43:03-06:00
[INFO] Final Memory: 13M/309M
[INFO] ------------------------------------------------------------------------
```

3. Validate the catalog in the following way.

`curl -X GET http://localhost:9082/catalog/rest/items`

You should now see a list of inventory items in your terminal.

```
[{"id":13405,"name":"Electric Card Collator","description":"The IBM 77 electric punched card collator performed many card filing and pulling operations. As a filing machine, the Type 77 fed and compared simultaneously two groups of punched cards: records already in file and records to be filed. These two groups were merged in correct numerical or alphabetical sequence. When operated for the purpose of pulling cards, the Type 77 made it possible for one group of cards to pull corresponding cards from another group. Introduced in 1937, the IBM 77 collator rented for 0 a month. It was capable of handling 240 cards a minute, and was 40.5 inches long and 51 inches high. IBM withdrew the Type 77 from marketing on November 27, 1957.","price":3899,"imgAlt":"IBM 77 Electric Punched Card Collator","img":"electric-card-punch.jpg","stock":1000}, _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _  _ _ _ ]
```
4. If you are done accessing the application, you can stop your server using the following command.

   `mvn liberty:stop-server -DtestServerHttpPort=9082`

Once you do this, you see the below messages.

```
[INFO] CWWKM2001I: Invoke command is [/Users/user@ibm.com/BlueCompute/refarch-cloudnative-micro-inventory/catalog/target/liberty/wlp/bin/server, stop, defaultServer].
[INFO] objc[69022]: Class JavaLaunchHelper is implemented in both /Library/Java/JavaVirtualMachines/jdk1.8.0_131.jdk/Contents/Home/jre/bin/java (0x1038944c0) and /Library/Java/JavaVirtualMachines/jdk1.8.0_131.jdk/Contents/Home/jre/lib/libinstrument.dylib (0x10398e4e0). One of the two will be used. Which one is undefined.
[INFO] Stopping server defaultServer.
[INFO] Server defaultServer stopped.
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 2.126 s
[INFO] Finished at: 2018-02-23T12:49:35-06:00
[INFO] Final Memory: 13M/309M
[INFO] ------------------------------------------------------------------------
```
