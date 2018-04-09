# Cloudnative MicroProfile Microservice Integration with MySQL Database Server 

## Inventory Service - MicroProfile

This repository contains the **MicroProfile** implementation of the **Inventory Service** which is a part of 'IBM Cloud Native Reference Architecture' suite, available at https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes

<p align="center">
  <a href="https://microprofile.io/">
    <img src="https://github.com/ibm-cloud-architecture/refarch-cloudnative-wfd/blob/microprofile/static/imgs/microprofile_small.png" width="300" height="100">
  </a>
</p>

1. [Introduction](#introduction)
2. [How it works](#how-it-works)
3. [Implementation](#implementation)
    1. [Liberty app accelerator](#liberty-app-accelerator)
    2. [Microprofile](#microprofile)
4. [Features and App details](#features)
5. [Building the app](#building-the-app)
6. [Running the app and stopping it](#running-the-app-and-stopping-it)
    1. [Pre-requisites](#pre-requisites)
    2. [Locally in JVM](#locally-in-jvm)
    3. [Locally in Containers](#locally-in-containers)
    4. [Locally in Minikube](#locally-in-minikube)
    5. [Remotely in ICP](#remotely-in-icp)
7. [DevOps Strategy](#devops-strategy)
8. [References](#references)

### Introduction

This project demonstrates the implementation of Inventory Microservice. The inventory microservice uses MySQL database as its datasource. It has the complete list of items of our store front. 

- Based on [MicroProfile](https://microprofile.io/).
- Uses MySQL as the inventory database.
- Devops - TBD
- Deployment options for local, Docker Container-based runtimes, Minikube environment and ICP/BMX.

### How it works

Inventory Microservice serves 'IBM Cloud Native Reference Architecture' suite, available at https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes, Microservice-based reference application. Though it is a part of a bigger application, Inventory service is itself an application in turn that manages the data from data store. Catalog Microservice serves as the cache to the Inventory.

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

You should also include a feature in [server.xml](https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/blob/microprofile/inventory/src/main/liberty/config/server.xml).

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

   `cd inventory`

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

### Running the app and stopping it

### Pre-requisites

**Set Up MYSQL on IBM Cloud**

1. [Provision](https://console.ng.bluemix.net/catalog/services/compose-for-mysql) and instance of MySQL into your Bluemix space.
    - Select name for your instance.
    - Click the `Create` button.
    
2. Refresh the page until you see `Status: Ready`.

3. Now obtain `MySQL` service credentials.
    - Click on `Service Credentials` tab.
    - Then click on the `View Credentials` dropdown next to the credentials.
    
4. See the `uri` field, which has the format `mysql://user:password@host:port/database`, and extract the following:
    - **user:** MySQL user.
    - **password:** MySQL password.
    - **host**: MySQL host.
    - **port:** MySQL port.
    - **database:** MySQL database.
    
5. Keep those credential handy for when deploying the Inventory service.

6. **Create `items` table and load sample data. You should see message _Data loaded to inventorydb.items._**

    ```
    # cd ..
    # cd mysql/scripts
    # bash load-data.sh {USER} {PASSWORD} {HOST} {PORT}
    ```
    
    - Replace `{USER}` with MySQL user.
    - Replace `{PASSWORD}` with MySQL password.
    - Replace `{HOST}` with MySQL host.
    - Replace `{PORT}` with MySQL port.

MySQL database is now setup in Compose.

In this case, your env variables will be 

```
export jdbcURL=jdbc:mysql://{HOST}:{PORT}/inventorydb?useSSL=false
export dbuser={USER}
export dbpassword={PASSWORD}
```
or alternatively you can run it in a container as well.

**Set Up MYSQL on Docker locally**

```
    # cd ..
    # cd mysql
```

- Build the docker image

`docker build -t mysql .`

- Run the container.

`docker run -p 9041:3306 -d --name mysql -e MYSQL_ROOT_PASSWORD=password mysql`

- Create `items` table and load sample data

`docker exec mysql ./load-data.sh root password 0.0.0.0 3306`

In this case, your jdbcURL will be 

```
export jdbcURL=jdbc:mysql://localhost:9041/inventorydb?useSSL=false
export dbuser=root
export dbpassword=password
```

**Set up RabbitMQ on Docker locally**

- Build the docker image

`docker pull rabbitmq`

- Run the container.

`docker run -d -p 5672:5672 -p 15672:15672  --name rabbitmq rabbitmq`

#### Locally in JVM

To run the Inventory microservice locally in JVM, please complete the [Building the app](#building-the-app) section.

#### Locally in Containers

To run Inventory microservice locally in container, you need [Docker](https://www.docker.com/) to be locally present in your system.

#### Locally in Minikube

To run the Inventory application locally on your laptop on a Kubernetes-based environment such as Minikube (which is meant to be a small development environment) we first need to get few tools installed:

- [Kubectl](https://kubernetes.io/docs/user-guide/kubectl-overview/) (Kubernetes CLI) - Follow the instructions [here](https://kubernetes.io/docs/tasks/tools/install-kubectl/) to install it on your platform.
- [Helm](https://github.com/kubernetes/helm) (Kubernetes package manager) - Follow the instructions [here](https://github.com/kubernetes/helm/blob/master/docs/install.md) to install it on your platform.

Finally, we must create a Kubernetes Cluster. As already said before, we are going to use Minikube:

- [Minikube](https://kubernetes.io/docs/getting-started-guides/minikube/) - Create a single node virtual cluster on your workstation. Follow the instructions [here](https://kubernetes.io/docs/tasks/tools/install-minikube/) to get Minikube installed on your workstation.

We not only recommend to complete the three Minikube installation steps on the link above but also read the [Running Kubernetes Locally via Minikube](https://kubernetes.io/docs/getting-started-guides/minikube/) page for getting more familiar with Minikube. We can learn there interesting things such as reusing our Docker daemon, getting the Minikube's ip or opening the Minikube's dashboard for GUI interaction with out Kubernetes Cluster.

**Set Up MYSQL on Minikube**

1. Start your minikube. Run the below command.

`minikube start`

You will see output similar to this.

```
Setting up certs...
Connecting to cluster...
Setting up kubeconfig...
Starting cluster components...
Kubectl is now configured to use the cluster.
```
2. To install Tiller which is a server side component of Helm, initialize helm. Run the below command.

`helm init`

If it is successful, you will see the below output.

```
$HELM_HOME has been configured at /Users/user@ibm.com/.helm.

Tiller (the helm server side component) has been installed into your Kubernetes Cluster.
Happy Helming!
```
3. Check if your tiller is available. Run the below command.

`kubectl get deployment tiller-deploy --namespace kube-system`

If it available, you can see the availability as below.

```
NAME            DESIRED   CURRENT   UP-TO-DATE   AVAILABLE   AGE
tiller-deploy   1         1         1            1           1m
```

4. Verify your helm before proceeding like below.

`helm version`

If your helm server version is below 2.5.0, please run the below command.

`helm init --upgrade --tiller-image gcr.io/kubernetes-helm/tiller:v2.5.0`

Make sure your versions by testing the versions.

You will see the below output.

```
Client: &version.Version{SemVer:"v2.4.2", GitCommit:"82d8e9498d96535cc6787a6a9194a76161d29b4c", GitTreeState:"clean"}
Server: &version.Version{SemVer:"v2.5.0", GitCommit:"012cb0ac1a1b2f888144ef5a67b8dab6c2d45be6", GitTreeState:"clean"}
```

5. Run the helm chart as below.

`helm install --name=bc stable/mysql`

6. Make sure your deployment is ready. To verify run this command and you should see the availability.

`kubectl get deployments`

Yow will see message like below.

```
NAME                      DESIRED   CURRENT   UP-TO-DATE   AVAILABLE   AGE
bc-mysql                  1         1         1            1           2m
```

7. Grab your root password run:

`kubectl get secret --namespace default bc-mysql -o jsonpath="{.data.mysql-root-password}" | base64 --decode; echo`

8. Run an Ubuntu pod that you can use as a client:

`kubectl run -i --tty ubuntu --image=ubuntu:16.04 --restart=Never -- bash -il`

You will enter the shell and see something like below.

```
root@ubuntu:/#
```

9. Install the mysql client:

`apt-get update && apt-get install mysql-client -y`

Youw will see something like below once done.

```
Setting up mysql-client-5.7 (5.7.21-0ubuntu0.16.04.1) ...
Setting up mysql-client (5.7.21-0ubuntu0.16.04.1) ...
Processing triggers for libc-bin (2.23-0ubuntu10) ...
```
10. Connect using the mysql cli, then provide your password you obtained previously in step 3.
    
`$ mysql -h bc-mysql -p`

You will see something like below.

```
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 252
Server version: 5.7.14 MySQL Community Server (GPL)

Copyright (c) 2000, 2018, Oracle and/or its affiliates. All rights reserved.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql>
```

11. Copy the contents of the script [here](https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/blob/microprofile/mysql/scripts/load-data.sql) and paste it in your console.

Your database is already now.

12. Enter `exit` to come out of mysql and enter `exit` to come out the ubuntu shell.

```
mysql> exit
Bye
root@ubuntu:/# exit
logout
```

### Locally in JVM

1. Set the JDBC URL before you start your application. The host and port depends on the service you use. You can run the MYSQL server locally on your system using the MYSQL docker container or use the [MYSQL Compose](https://www.ibm.com/cloud/compose/mysql) available in [IBM Cloud](https://www.ibm.com/cloud/).

   ```
   export jdbcURL=jdbc:mysql://<Your host>:<Port>/inventorydb?useSSL=false
   export dbuser=<DB_USER_NAME>
   export dbpassword=<PASSWORD>
   ```

2. Start your server.

   `mvn liberty:start-server -DtestServerHttpPort=9081`

   You will see the below.
```
[INFO] Starting server defaultServer.
[INFO] Server defaultServer started with process ID 62300.
[INFO] Waiting up to 30 seconds for server confirmation:  CWWKF0011I to be found in /Users/user@ibm.com/BlueCompute/refarch-cloudnative-micro-inventory/inventory/target/liberty/wlp/usr/servers/defaultServer/logs/messages.log
[INFO] CWWKM2010I: Searching for CWWKF0011I in /Users/user@ibm.com/BlueCompute/refarch-cloudnative-micro-inventory/inventory/target/liberty/wlp/usr/servers/defaultServer/logs/messages.log. This search will timeout after 30 seconds.
[INFO] CWWKM2015I: Match number: 1 is [22/2/18 15:19:34:512 CST] 00000019 com.ibm.ws.kernel.feature.internal.FeatureManager            A CWWKF0011I: The server defaultServer is ready to run a smarter planet..
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 23.456 s
[INFO] Finished at: 2018-02-22T15:19:34-06:00
[INFO] Final Memory: 13M/309M
[INFO] ------------------------------------------------------------------------
```
3. If you are done accessing the application, you can stop your server using the following command.

   `mvn liberty:stop-server -DtestServerHttpPort=9081`

Once you do this, you see the below messages.

```
[INFO] CWWKM2001I: Invoke command is [/Users/user@ibm.com/BlueCompute/refarch-cloudnative-micro-inventory/inventory/target/liberty/wlp/bin/server, stop, defaultServer].
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

### Locally in Containers

To run the application in docker, we first need to define a Docker file.

#### Docker file

We are using Docker to containerize the application. With Docker, you can pack, ship, and run applications on a portable, lightweight container that can run anywhere virtually.

```
FROM websphere-liberty:microProfile

MAINTAINER IBM Java engineering at IBM Cloud

COPY /target/liberty/wlp/usr/servers/defaultServer /config/
COPY target/liberty/wlp/usr/shared /opt/ibm/wlp/usr/shared/

# Install required features if not present
RUN installUtility install --acceptLicense defaultServer

CMD ["/opt/ibm/wlp/bin/server", "run", "defaultServer"]

# Upgrade to production license if URL to JAR provided
ARG LICENSE_JAR_URL
RUN \
  if [ $LICENSE_JAR_URL ]; then \
    wget $LICENSE_JAR_URL -O /tmp/license.jar \
    && java -jar /tmp/license.jar -acceptLicense /opt/ibm \
    && rm /tmp/license.jar; \
  fi
```

- The `FROM` instruction sets the base image. You're setting the base image to `websphere-liberty:microProfile`.
- The `MAINTAINER` instruction sets the Author field. Here it is `IBM Java engineering at IBM Cloud`.
- The `COPY` instruction copies directories and files from a specified source to a destination in the container file system.
  - You're copying the `/target/liberty/wlp/usr/servers/defaultServer` to the `config` directory in the container.
  - You're replacing the contents of `/opt/ibm/wlp/usr/shared/` with the contents of `target/liberty/wlp/usr/shared`.
- The `RUN` instruction runs the commands.
  - The first instruction gets the Opentracing Zipkin feature and installs it in your server.
  - The second instruction is a precondition to install all the utilities in the server.xml file. You can use the RUN command to install the utilities on the base image.
- The `CMD` instruction provides defaults for an executing container.

#### Running the application locally in a docker container

1. Build the docker image.

`docker build -t inventory:microprofile .`

Once this is done, you will see something similar to the below messages.
```
Successfully built 02a2348107d9
Successfully tagged inventory:microprofile
```
You can see the docker images by using this command.

`docker images`

```
REPOSITORY                     TAG                 IMAGE ID            CREATED             SIZE
inventory                      microprofile        02a2348107d9        36 seconds ago      413MB
```

2. Run the docker image.

`docker run -p 9180:9080 --name inventory -t --link mysql:mysql --env jdbcURL=jdbc:mysql://mysql:3306/inventorydb?useSSL=false --env dbuser=root --env dbpassword=password inventory:microprofile`

When it is done, you will see the following output.
```
[AUDIT   ] CWWKE0001I: The server defaultServer has been launched.
[AUDIT   ] CWWKE0100I: This product is licensed for development, and limited production use. The full license terms can be viewed here: https://public.dhe.ibm.com/ibmdl/export/pub/software/websphere/wasdev/license/base_ilan/ilan/17.0.0.3/lafiles/en.html
[AUDIT   ] CWWKZ0058I: Monitoring dropins for applications. 
[AUDIT   ] CWWKT0016I: Web application available (default_host): http://f0d52b900623:9080/health/
[AUDIT   ] CWWKT0016I: Web application available (default_host): http://f0d52b900623:9080/jwt/
[AUDIT   ] CWWKT0016I: Web application available (default_host): http://f0d52b900623:9080/ibm/api/
[AUDIT   ] CWWKT0016I: Web application available (default_host): http://f0d52b900623:9080/metrics/
[AUDIT   ] CWWKT0016I: Web application available (default_host): http://f0d52b900623:9080/inventory/
[AUDIT   ] CWWKZ0001I: Application inventory-1.0-SNAPSHOT started in 9.675 seconds.
[AUDIT   ] CWWKF0012I: The server installed the following features: [microProfile-1.2, mpFaultTolerance-1.0, servlet-3.1, ssl-1.0, jndi-1.0, mpHealth-1.0, appSecurity-2.0, jsonp-1.0, mpConfig-1.1, jaxrs-2.0, jaxrsClient-2.0, concurrent-1.0, jwt-1.0, mpMetrics-1.0, mpJwt-1.0, json-1.0, cdi-1.2, distributedMap-1.0].
[AUDIT   ] CWWKF0011I: The server defaultServer is ready to run a smarter planet.
```
4. Once you are done accessing the application, you can come out of the process. You can do this by pressing Ctrl+C on the command line where the server was started.

5. You can also remove the container if desired. This can be done in the following way.

`docker ps`

```
CONTAINER ID        IMAGE                        COMMAND                  CREATED             STATUS              PORTS                              NAMES
f0d52b900623        inventory:microprofile   "/opt/ibm/wlp/bin/se…"   4 minutes ago       Up 4 minutes        9443/tcp, 0.0.0.0:9180->9080/tcp   inventory
```

Grab the container id.

- Do `docker stop <CONTAINER ID>`
In this case it will be, `docker stop f0d52b900623`
- Do `docker rm <CONTAINER ID>`
In this case it will be, `docker rm f0d52b900623`

### Locally in Minikube

#### Setting up your environment

1. Start your minikube. Run the below command.

`minikube start`

You will see output similar to this.

```
Setting up certs...
Connecting to cluster...
Setting up kubeconfig...
Starting cluster components...
Kubectl is now configured to use the cluster.
```
2. To install Tiller which is a server side component of Helm, initialize helm. Run the below command.

`helm init`

If it is successful, you will see the below output.

```
$HELM_HOME has been configured at /Users/user@ibm.com/.helm.

Tiller (the helm server side component) has been installed into your Kubernetes Cluster.
Happy Helming!
```
3. Check if your tiller is available. Run the below command.

`kubectl get deployment tiller-deploy --namespace kube-system`

If it available, you can see the availability as below.

```
NAME            DESIRED   CURRENT   UP-TO-DATE   AVAILABLE   AGE
tiller-deploy   1         1         1            1           1m
```

4. Verify your helm before proceeding like below.

`helm version`

You will see the below output.

```
Client: &version.Version{SemVer:"v2.4.2", GitCommit:"82d8e9498d96535cc6787a6a9194a76161d29b4c", GitTreeState:"clean"}
Server: &version.Version{SemVer:"v2.5.0", GitCommit:"012cb0ac1a1b2f888144ef5a67b8dab6c2d45be6", GitTreeState:"clean"}
```
#### Running the application on Minikube

1. Build the docker image.

Before building the docker image, set the docker environment.

- Run the below command.

`minikube docker-env`

You will see the output similar to this.

```
export DOCKER_TLS_VERIFY="1"
export DOCKER_HOST="tcp://192.168.99.100:2376"
export DOCKER_CERT_PATH="/Users/user@ibm.com/.minikube/certs"
export DOCKER_API_VERSION="1.23"
# Run this command to configure your shell:
# eval $(minikube docker-env)
```
- For configuring your shell, run the below command.

`eval $(minikube docker-env)`

- Now run the docker build.

`docker build -t inventory:v1.0.0 .`

If it is a success, you will see the below output.

```
Successfully built 36d1cf24d7ad
Successfully tagged inventory:v1.0.0
```
2. Run the helm chart as below.

`helm install --name=inventory chart/inventory`

Yow will see message like below.

```
==> v1beta1/Deployment
NAME                  DESIRED  CURRENT  UP-TO-DATE  AVAILABLE  AGE
inventory-deployment  1        1        1           0          0s
```
Please wait till your deployment is ready. To verify run the below command and you should see the availability.

`kubectl get deployments`

You will see something like below.

```
==> v1beta1/Deployment
NAME                     DESIRED  CURRENT  UP-TO-DATE  AVAILABLE  AGE
inventory-deployment      1         1         1            1           8m
```





