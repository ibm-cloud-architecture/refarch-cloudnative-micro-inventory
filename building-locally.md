# Run Inventory Service locally

## Table of Contents

* [Building Inventory using Maven](#building-inventory-using-maven)
* [Setting up MySQL](#setting-up-mysql)
* [Setting up Zipkin](#setting-up-zipkin) (Optional)
* [Running Inventory and stopping it](#running-inventory-and-stopping-it)
* [Deploying Inventory using Helm charts](#deploying-inventory-using-helm-charts)

## Building Inventory using Maven

To build the application, we used maven build. Maven is a project management tool that is based on the Project Object Model (POM). Typically, people use Maven for project builds, dependencies, and documentation. Maven simplifies the project build. In this task, you use Maven to build the project.

1. Clone this repository.

   `git clone https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory.git`
   
   `cd refarch-cloudnative-micro-inventory/`

2. Checkout MicroProfile branch.

   `git checkout microprofile`

3. Run this command. This command builds the project and installs it.

   `mvn clean install`
   
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
By default, the application runs on [WebSphere Liberty with Web Profile](https://developer.ibm.com/wasdev/websphere-liberty/). You can also run it on [Open Liberty](https://openliberty.io/) as follows.

`mvn clean install -Popenliberty`

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
[INFO] Total time: 29.264 s
[INFO] Finished at: 2018-10-23T17:55:10-05:00
[INFO] Final Memory: 29M/449M
[INFO] ------------------------------------------------------------------------
```

## Setting up MySQL

To set up MySQL locally, we are running it as a docker container. You need [Docker](https://www.docker.com/) as a prerequisite.

To run MySQL on docker locally, run the below commands.

```
cd mysql
```

1. Build the docker image.

`docker build -t mysql .`

2. Run the container.

`docker run -p 9041:3306 -d --name mysql -e MYSQL_ROOT_PASSWORD=password mysql`

3. It creates `items` table and loads the sample data.

```
cd ..
```

In this case, your environment variables will be:

```
export jdbcURL=jdbc:mysql://localhost:9041/inventorydb?useSSL=false
export dbuser=root
export dbpassword=password
```

## Setting up Zipkin

This is an optional step.

In our sample application, we used Zipkin as our distributed tracing system.

If you want to access the traces for inventory service, run Zipkin as a docker container locally. You can find the instructions and more details [here](https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes/blob/microprofile/Zipkin/README.md)


## Running Inventory and stopping it

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

## Deploying Inventory using Helm Charts

The most convenient solution to start the Inventory service uses [Kubernetes](https://kubernetes.io/), as a container orchestration tool, and [Helm](https://helm.sh/), to deploy the necessary containers with the correct configuration. 

The Inventory service has a required dependency, MySQL, which first must be obtained with:

```
helm update dependencies
```

Then run the Inventory service standalone with:

```
helm install --set rabbit.enabled=true chart/inventory
```

RabbitMQ must be enabled to satisfy the readiness probe. It is set to `false` by default because normally it's provided by the Orders service.
