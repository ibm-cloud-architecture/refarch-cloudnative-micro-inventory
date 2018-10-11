###### refarch-cloudnative-micro-inventory

## Deploy Inventory Application on Open Liberty

You can run your Spring Boot applications on Open Liberty. Open Liberty will be the packaged server and the application will run on it instead of the default embedded server. Below are steps to run the Inventory application on Open Liberty.

## Table of Contents
+ [Deploy the MySQL Docker Container](#deploy-the-mysql-docker-container)
+ [Populate the MySQL Database](#populate-the-mysql-database)
+ [Deploy the Inventory locally](#deploy-the-inventory-locally)
+ [Deploy the Inventory Docker Container](#deploy-the-inventory-docker-container)

## Deploy the MySQL Docker Container

Run the MySQL as a docker container. Below is the command to start it.

```bash
# Start a MySQL Container with a database user, a password, and create a new database
$ docker run --name inventorymysql \
    -e MYSQL_ROOT_PASSWORD=admin123 \
    -e MYSQL_USER=dbuser \
    -e MYSQL_PASSWORD=password \
    -e MYSQL_DATABASE=inventorydb \
    -p 9041:3306 \
    -d mysql:5.7.14
```

## Populate the MySQL Database

In order for Inventory to make use of the MySQL database, the database needs to be populated first. To do so, run the following commands:
```bash
$ docker exec -it inventorymysql bash
```

You will enter the container now and run the below command.
```
# mysql -udbuser -ppassword
```

Copy the lines [here](https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/blob/spring/scripts/mysql_data.sql) and paste them there to load the data.

Enter `exit` to come out of mysql as below.
```
mysql> exit
```
Enter `exit` again to come out of the conatiner.

```
# exit
```

## Deploy the Inventory locally

In this section you will build and run the Spring Boot application on Open Liberty locally using Maven. Before we show you how to do so, you will need to deploy a MySQL Docker container and populate it with data as shown in the [Deploy the MySQL Docker Container](#deploy-the-mysql-docker-container) and [Populate the MySQL Database](#populate-the-mysql-database) sections, respectively.

Once MySQL is ready and populated, we can run the Spring Boot Inventory application on Open Liberty locally as follows:

1. Open [`src/main/resources/application.yml`](src/main/resources/application.yml) file, enter the following values for the fields under `spring.datasource`, and save the file:
    * **url:** jdbc:mysql://localhost:9041/inventorydb?useSSL=false
    * **username:** dbuser
    * **password:** password
    * **port:** 9041

2. Build the application as follows.
```bash
$ mvn package
```

3. Run the application on localhost.
```bash
$ java -jar target/liberty-inventory-spring-0.1-SNAPSHOT.jar
```

Once it is deployed, you will see something like below.

```
2018-10-10 13:51:41.669  INFO 14858 --- [cutor-thread-10] o.s.j.e.a.AnnotationMBeanExporter        : Registering beans for JMX exposure on startup
2018-10-10 13:51:41.683  INFO 14858 --- [cutor-thread-10] o.s.c.support.DefaultLifecycleProcessor  : Starting beans in phase 0
2018-10-10 13:51:41.771  INFO 14858 --- [cutor-thread-10] inventory.Application                    : Started Application in 9.968 seconds (JVM running for 14.676)
Inventory microservice is ready for business...
Will not use Message Hub due to invalid configuration
[AUDIT   ] CWWKZ0001I: Application thin-liberty-inventory-spring-0.1-SNAPSHOT started in 10.874 seconds.
[AUDIT   ] CWWKF0012I: The server installed the following features: [servlet-4.0, springBoot-1.5].
[AUDIT   ] CWWKF0011I: The server BoostServer is ready to run a smarter planet.
```

4. Validate. You should get a list of all inventory items:
```bash
$ curl http://localhost:8080/micro/inventory
```

That's it, you have successfully deployed and tested the Inventory microservice running on Spring Boot using Open Liberty server.

## Deploy the Inventory Docker Container

In this section you will run the Spring Boot application on Open Liberty using Docker. Before we show you how to do so, you will need to deploy a MySQL Docker container and populate it with data as shown in the [Deploy the MySQL Docker Container](#deploy-the-mysql-docker-container) and [Populate the MySQL Database](#populate-the-mysql-database) sections, respectively.

Once MySQL is ready and populated, we can run the Spring Boot Inventory application on OpenLiberty dcoker as follows:

1. Open [`src/main/resources/application.yml`](src/main/resources/application.yml) file, enter the following values for the fields under `spring.datasource`, and save the file:
    * **url:** jdbc:mysql://inventorymysql:3306/inventorydb?useSSL=false
    * **username:** dbuser
    * **password:** password
    * **port:** 3306

2. Modify your pom to generate the docker image. Follow the below steps.
- Open [`pom.xml`](pom.xml)
- Modify the `boost-maven-plugin` as below.

```
            <plugin>
                <groupId>io.openliberty.boost</groupId>
                <artifactId>boost-maven-plugin</artifactId>
                <version>0.1</version>
                <executions>
                  <execution>
                    <!-- <phase>package</phase>
                    <goals>
                        <goal>package</goal>
                    </goals> -->
                    <goals>
                      <goal>docker-build</goal>
                    </goals>
                  </execution>
                </executions>
            </plugin>
```

3. To deploy the Inventory container, run the following commands:

Before building the Docker image, make sure there are no other dockerfiles in your repo. If a dockerfile already exists, please remove it.

```bash
# Build the Docker Image
$ mvn clean install 
```

Once done, a docker image named `liberty-inventory-spring:latest` will be built automatically for you.

Run the docker container as follows.

```
# Start the Inventory Container
$ docker run --name inventory \
    -e MYSQL_HOST=inventorymysql \
    -e MYSQL_PORT=3306 \
    -e MYSQL_USER=dbuser \
    -e MYSQL_PASSWORD=password \
    -e MYSQL_DATABASE=inventorydb \
    -p 8080:8080 \
    -p 9081:9080 \
    -p 8443:9443 \
    --link inventorymysql:inventorymysql \
    -d liberty-inventory-spring:latest
```

If everything works successfully, you should be able to get some data when you run the following command:
```bash
$ curl http://localhost:9081/micro/inventory
```
