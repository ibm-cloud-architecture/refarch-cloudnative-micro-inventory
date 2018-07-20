# Run Catalog Service locally

## Table of Contents

- [Building the app](#building-the-app)
- [Setting up Elasticsearch](#setting-up-elasticsearch)
- [Setting up Zipkin](#setting-up-zipkin)
- [Running the app and stopping it](#running-the-app-and-stopping-it)

## Building the app

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
## Setting up Elasticsearch

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

## Setting up Zipkin

This is an optional step.

In our sample application, we used Zipkin as our distributed tracing system.

If you want to access the traces for catalog service, run Zipkin as a docker container locally. You can find the instructions and more details [here](https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes/blob/microprofile/Zipkin/README.md)

## Running the app and stopping it

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
