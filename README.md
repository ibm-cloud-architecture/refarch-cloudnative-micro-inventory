# refarch-cloudnative-micro-inventory

## Spring Boot Netflix OSS Microservice Apps Integration with ElasticSearch and MySQL Database Server

*This project is part of the 'IBM Cloud Native Reference Architecture' suite, available at
https://github.com/ibm-cloud-architecture/refarch-cloudnative*

## Table of Contents
- **[Introduction](#introduction)**
  - [APIs](#apis)
- **[Pre-requisites](#pre-requisites)**
  - [Message Hub](#message-hub)
- **[Deploy Inventory and Catalog Microservices using DevOps Toolchain](#deploy-inventory-and-catalog-microservices-using-devops-toolchain)**
- **[Run Inventory and Catalog Locally](#run-inventory-and-catalog-locally)**
  - [Deploy MySQL on local docker container](#deploy-mysql-on-local-docker-container)
  - [Deploy Elasticsearch on local docker container](#deploy-elasticsearch-on-local-docker-container)
  - [Run Inventory Service application on localhost](#run-inventory-service-application-on-localhost)
  - [Run Catalog Service application on localhost](#run-catalog-service-application-on-localhost)
- **[Deploy Inventory and Catalog on local Docker Containers](#)**
  - [Run Inventory Service application on local docker container](#run-inventory-service-application-on-local-docker-container)
  - [Run Catalog Service application on local docker container](#run-catalog-service-application-on-local-docker-container)
- **[Deploy Inventory and Catalog on Bluemix Containers](#)**
  - [Deploy MySQL on Bluemix container](#deploy-mysql-on-bluemix-container)
  - [Deploy Elasticsearch on Bluemix container](#deploy-elasticsearch-on-bluemix-container) 
  - [Deploy Inventory Service application on Bluemix container](#deploy-inventory-service-application-on-bluemix-container)
  - [Deploy Catalog Service application on Bluemix container](#deploy-catalog-service-application-on-bluemix-container)
- **[Using Compose for Production Databases](#using-compose-for-production-databases)**
  - [Deploy MySQL on Bluemix using Compose](#deploy-mysql-on-bluemix-using-compose)
  - [Deploy Elasticsearch on Bluemix using Compose](#deploy-elasticsearch-on-bluemix-using-compose)

## Introduction

This project is built to demonstrate how to build two Microservices applications using Spring Boot and Docker container.. The first application (`Inventory`) uses MySQL database as its datasource. The second and publicly available application (`Catalog`) serves as a cache to `Inventory` by leveraging `Elasticsearch` as its datasource.

Here is an overview of the project's features:
- Leverage [`Spring Boot`](https://projects.spring.io/spring-boot/) framework to build a Microservices application.
- Use [`Spring Data JPA`](http://projects.spring.io/spring-data-jpa/) to persist data to MySQL database and Elasticsearch.
- Uses `MySQL` as the inventory database.
- [`Elasticsearch`](https://github.com/elastic/elasticsearch) is used as the `Catalog` microservice's data source.
- Uses [`MessageHub`](https://console.ng.bluemix.net/catalog/message-hub/) to receive messages that act as triggers to synchronize Inventory database with `Elasticsearch`.
- Integrate with [`Netflix Eureka`](https://github.com/Netflix/eureka) framework.
- Deployment option for [`IBM Bluemix Container`](https://www.ibm.com/cloud-computing/bluemix/containers) runtime.

**Architecture Diagram**

![Inventory/Catalog Diagram](inventory-catalog.png)

### APIs
You can use cURL or Chrome POSTMAN to send get/post/put/delete requests to the application.
- Get all items in inventory:
`http://<catalog_hostname>/micro/items`

- Get item by id:
`http://<catalog_hostname>/micro/items/{id}`

- Example curl command to get al items in localhost:
`curl -X GET "http://localhost:8081/micro/items"`

## Pre-requisites:
- Clone git repository before getting started.

  ```
  git clone http://github.com/refarch-cloudnative-micro-inventory.git
  cd refarch-cloudnative-micro-inventory
  ```
- You need a docker engine running on localhost to host container(s). [Click for instructions](https://docs.docker.com/engine/installation/).
- You need to [Provision `MessageHub`](#message-hub) service instance.


### Message Hub
1. Provision an instance of [Message Hub](https://console.ng.bluemix.net/catalog/services/message-hub) into your Bluemix space.
  - Select name for your instance.
  - Click the `Create` button.
2. Refresh the page until you see `Status: Ready`.
3. Now obtain `Message Hub` service credentials.
  - Click on `Service Credentials` tab.
  - Then click on the `View Credentials` dropdown next to the credentials.
4. You will need the following:
  - **kafka_rest_url:** Needed to query and create `topics`.
  - **api_key:** Needed to use the Message Hub REST API.
  - **user:** Message Hub user.
  - **password:** Message Hub password.
  - **kafka_brokers_sasl:** Message Hub kafka brokers, which are in charge of receiving and sending messages for specific topics.
5. Keep those credential handy as they will be needed throughout the rest of this document.

## Deploy Inventory and Catalog Microservices using DevOps Toolchain
You can use the following button to deploy the Inventory and Catalog microservices to Bluemix, or you can follow the manual instructions in the following sections. If you decide on the toolchain button, you have to fulfill the following pre-requisites:
- **[Provision](#message-hub) a Message Hub service instance in your Bluemix Space**.
  - The toolchain will automatically pick up `Message Hub` credentials.
- **Deploy MySQL in your Bluemix Space**.
  - Options:
    - [Deploy MySQL on Bluemix container](#deploy-mysql-on-bluemix-container)
    - [Deploy MySQL on Bluemix using Compose](#deploy-mysql-on-bluemix-using-compose).
  - The toolchain will ask you to enter the `username`, `password`, `ip`, `port`, and `database` for MySQL.
- **Deploy Elasticsearch in your Bluemix Space**.
  - Options:
    - [Deploy Elasticsearch on Bluemix container](#deploy-elasticsearch-on-bluemix-container) 
    - [Deploy Elasticsearch on Bluemix using Compose](#deploy-elasticsearch-on-bluemix-using-compose)
  - The toolchain will ask you to enter the `username`, `password`, and `URL` for Elasticsearch.


[![Create BlueCompute Deployment Toolchain](https://console.ng.bluemix.net/devops/graphics/create_toolchain_button.png)](https://console.ng.bluemix.net/devops/setup/deploy?repository=https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory.git)


## Run Inventory and Catalog Locally
In this section you will learn how to build and run the Inventory and Catalog apps locally.

### Deploy MySQL on local docker container
1. **Change to the mysql directory**.
    ```
    # cd mysql
    ```

2. **Create MySQL container with database `inventorydb`. This database can be connected at `<docker-host-ipaddr/hostname>:3306` as `dbuser` using `password`**.
    ```
    # docker build -t cloudnative/mysql .
    # docker run --name mysql -d -p 3306:3306 \
      -e MYSQL_ROOT_PASSWORD=admin123 \
      -e MYSQL_USER=dbuser \
      -e MYSQL_PASSWORD=password \
      -e MYSQL_DATABASE=inventorydb \
      cloudnative/mysql
    ```

3. **Create `items` table and load sample data. You should see _Data loaded to inventorydb.items_**.
    ```
    # docker exec -it mysql bash load-data.sh
    ```

4. **Verify, there should be 12 rows in the table**.
    ```
    # docker exec -it mysql bash
    # mysql -u ${MYSQL_USER} -p${MYSQL_PASSWORD} ${MYSQL_DATABASE}
    mysql> select * from items;
    mysql> quit
    # exit
    ```

5. **Get container Private IP Address. This will be used when [Running inventory from local docker container](#run-inventory-service-application-on-local-docker-container)**. 
    ```
    # docker inspect mysql | grep -i ipaddress
            "SecondaryIPAddresses": null,
            "IPAddress": "172.17.0.2",
                    "IPAddress": "172.17.0.2",
    ```

6. Use `jdbc:mysql://{mysql_ip}:3306/inventorydb` as your `spring.datasource.url` when deploying `Inventory on local Docker Container`.

Inventory database is now setup in local container.

### Deploy Elasticsearch on local docker container

1. **Run docker container locally. This will download the elasticsearch image (if it does not exist already) and run it.**
    ```
    docker run --name elasticsearch -d -p 9200:9200 elasticsearch
    ```

2. **Validate**.
  ```
  # curl http://localhost:9200

  {
    "name" : "1-LsU37",
    "cluster_name" : "elasticsearch",
    "cluster_uuid" : "1fn3QtZpTOWOhEpEM01ZAw",
    "version" : {
      "number" : "5.2.0",
      "build_hash" : "24e05b9",
      "build_date" : "2017-01-24T19:52:35.800Z",
      "build_snapshot" : false,
      "lucene_version" : "6.4.0"
    },
    "tagline" : "You Know, for Search"
  }
  ```

3. Use `http://localhost:9200` as your `elasticsearch.url` when running `Catalog` and `Inventory` **locally**.


### Run Inventory Service application on localhost
In this section you will run the Spring Boot application to run on your localhost.

1. **Change to `inventory` directory**.
    ```
    cd inventory
    ```

2. **If not already done, [Deploy `MySQL` on local docker container](#deploy-mysql-on-local-docker-container)**.
  - Open `src/main/resources/application.yml`, go to `datasource` section.
  - Type `url` as `jdbc:mysql://127.0.0.1/inventorydb`, where `127.0.0.1` means `localhost`
  - Make sure the `user`, `password`, and `port` match those of local container.

3. **If not already done, [Deploy `Elasticsearch` on local docker container](#deploy-elasticsearch-on-local-docker-container)**.
  - Open `src/main/resources/application.yml`, go to `elasticsearch` section.
  - Type `http://localhost:9200` on the `url` field.

4. **If not already done, [Provision `Message Hub` service instance](#message-hub)**.
  - After provisioning, go to instance `Service Credentials` tab on Bluemix, then press `View credentials`.
  - Open `src/main/resources/application.yml`, go to `message_hub` section, then copy and paste required `message_hub` fields using credentials from above.

5. **Build the application**.
    ```
    # ./gradlew build -x test
    ```

6. **Run the application on localhost**.
    ```
    # java -jar build/libs/micro-inventory-0.0.1.jar
    ```

7. **Validate. You should get a list of all inventory items**.
    ```
    # curl http://localhost:8080/micro/inventory
    ```

### Run Catalog Service application on localhost
In this section you will run the Catalog Spring Boot application to run on your localhost.

1. **Change to `catalog` directory**.
    ```
    cd catalog
    ```

2. **If not already done, [Deploy `Elasticsearch` on local docker container](#deploy-elasticsearch-on-local-docker-container)**.
  - Open `src/main/resources/application.yml`, go to `elasticsearch` section.
  - Type `http://localhost:9200` on the `url` field.

3. **Build the application**.
    ```
    # ./gradlew build -x test
    ```

4. **Run the application on localhost**.
    ```
    # java -jar build/libs/micro-catalog-0.0.1.jar
    ```

5. **Validate. You should get a list of all catalog items**.
    ```
    # curl http://localhost:8081/micro/items
    ```

## Deploy Inventory and Catalog on local Docker Containers
In this section you will learn how to package the Inventory and Catalog apps as docker images and deploy in local Docker environment.

### Run Inventory Service application on local docker container

1. **Change to `inventory` directory**.
    ```
    cd inventory
    ```

2. **If not already done, [Deploy `MySQL` on local docker container](#deploy-mysql-on-local-docker-container)**.
  - Obtain the MySQL `Container Private IP Address`, `database username`, and `database password` from link above and have them ready for deployment in `Step 6`.

3. **If not already done, [Deploy `Elasticsearch` on local docker container](#deploy-elasticsearch-on-local-docker-container)**.
  - Obtain the Elasticsearch `Container Private IP Address` from link above and have it ready for deployment in `Step 6`.
 
4. **If not already done, [Provision `Message Hub` service instance](#message-hub)**.
  - After provisioning, go to instance `Service Credentials` tab on Bluemix, then press `View credentials`.
  - Obtain the Message HUB `user`, `password`, `api_key`, `kafka_rest_url`, and `kafka_brokers_sasl` from link above and have them ready for deployment in `Step 6`.

5. **Build container image**
    ```
    # ./gradlew build -x test
    # ./gradlew docker
    # cd docker
    # docker build -t cloudnative/inventoryservice .
    ```

5. **Start the application in docker container**.
  ```
  # docker run -d -p 8080:8080 --name inventoryservice \
    -e "spring.datasource.url=jdbc:mysql://{mysql_ip}:3306/inventorydb" \
    -e "spring.datasource.username={mysql_user}" \
    -e "spring.datasource.password={mysql_password}" \
    -e "elasticsearch.url={es_url}" \
    -e "message_hub.user={mh_user}" \
    -e "message_hub.password={mh_password}" \
    -e "message_hub.api_key={mh_api_key}" \
    -e "message_hub.kafka_rest_url={mh_kafka_rest_url}" \
    -e "message_hub.kafka_brokers_sasl[0]={mh_kafka_broker_0}" \
    -e "message_hub.kafka_brokers_sasl[1]={mh_kafka_broker_1}" \
    -e "message_hub.kafka_brokers_sasl[2]={mh_kafka_broker_2}" \
    -e "message_hub.kafka_brokers_sasl[3]={mh_kafka_broker_3}" \
    -e "message_hub.kafka_brokers_sasl[4]={mh_kafka_broker_4}" \
    cloudnative/inventoryservice
  ```

  - Replace `{mysql_container_ip}` with the MySQL container instance IP address.
  - Replace `{mysql_user}` with database username.
  - Replace `{mysql_password}` with database user password.
  - Replace `{es_url}` with the Bluemix container private ip address.
  - Replace `{mh_user}` with Message Hub `user`.
  - Replace `{mh_password}` with Message Hub `password`.
  - Replace `{mh_api_key}` with Message Hub `api_key`.
  - Replace `{mh_kafka_rest_url}` with Message Hub `kafka_rest_url`.
  - Replace all the `{mh_kafka_broker[x]}` with all the URLs listed in Message Hub `kafka_brokers_sasl`.

6. **Validate. You should get a list of all inventory items**.
    ```
    # curl http://localhost:8080/micro/inventory
    ```

### Run Catalog Service application on local docker container
In this section you will deploy the Catalog Spring Boot application to run in a local docker container.

1. **Change to `catalog` directory**.
    ```
    cd catalog
    ```

2. **If not already done, [Deploy Elasticsearch on local docker container](#deploy-elasticsearch-on-local-docker-container)**.
  - Obtain the Elasticsearch `Container Private IP Address` from link above and have it ready for deployment in `Step 4`.

3. **Build container image**
    ```
    # ./gradlew build -x test
    # ./gradlew docker
    # cd docker
    # docker build -t cloudnative/catalogservice .
    ```

4. **Start the application in docker container**.
  ```
  # docker run -d -p 8081:8081 --name catalogservice \
  -e "eureka.client.fetchRegistry=false" \
  -e "eureka.client.registerWithEureka=false" \
  -e "elasticsearch.url={es_url}" \
  cloudnative/catalogservice"
  ```

  - Replace `{es_url}` with the Bluemix container private ip address.

5. **Validate. You should get a list of all catalog items**.
    ```
    # curl http://localhost:8081/micro/items
    ```

## Deploy Inventory and Catalog on Bluemix Containers
In this section you will learn how to deploy the Inventory and Catalog apps in Bluemix containers.

### Deploy MySQL on Bluemix container

1. **Log in to your Bluemix account**.
    ```
    # cf login -a <bluemix-api-endpoint> -u <your-bluemix-user-id>
    ```

2. **Set target to use your Bluemix Org and Space**.
    ```
    # cf target -o <your-bluemix-org> -s <your-bluemix-space>
    ```

3. **Log in to IBM Containers plugin**.
    ```
    # cf ic login
    ```

4. **Check that your organization has set a namespace**.
    ```
    # cf ic namespace get
    ```

5. **If there is no namespace set for your organization, then set a namespace**.
    ```
    # cf ic namespace set
    ```

6. **Change to the mysql directory**.
    ```
    # cd mysql
    ```

7. **Build docker image using the Dockerfile from repo**.
    ```
    # docker build -t cloudnative/mysql .
    ```

8. **Tag and push mysql database server image to your Bluemix private registry namespace**.
    ```
    # docker tag cloudnative/mysql registry.ng.bluemix.net/$(cf ic namespace get)/mysql:cloudnative
    # docker push registry.ng.bluemix.net/$(cf ic namespace get)/mysql:cloudnative
    ```

9. **Create MySQL container with database `inventorydb`. This database can be connected at `<docker-host-ipaddr/hostname>:3306` as `dbuser` using `Pass4dbUs3R`**.

    _It is recommended to change the default passwords used here._
    ```
    # cf ic run -m 512 --name mysql -p 3306:3306 \
    -e MYSQL_ROOT_PASSWORD=Pass4Admin123 \
    -e MYSQL_USER=dbuser \
    -e MYSQL_PASSWORD=Pass4dbUs3R \
    -e MYSQL_DATABASE=inventorydb \
    registry.ng.bluemix.net/$(cf ic namespace get)/mysql:cloudnative
    ```

10. **Before loading sample data. Check that mysql container is running**.
    ```
    # cf ic ps | grep mysql
    7618b4a1-6d9        registry.ng.bluemix.net/chrisking/mysql:cloudnative       ""                  4 minutes ago       Running             3306/tcp                       mysql
    ```

11. **Create `items` table and load sample data. You should see message _Data loaded to inventorydb.items._**
    ```
    # cf ic exec -it mysql bash load-data.sh
    ```

12. **Verify, there should be 12 rows in the table**.
    ```
    # cf ic exec -it mysql bash
    # mysql -u ${MYSQL_USER} -p${MYSQL_PASSWORD} ${MYSQL_DATABASE}
    mysql> select * from items;
    mysql> quit
    # exit
    ```

13. **Get container Private IP Address. This will be used when running [Inventory Service application from IBM Bluemix container](#deploy-inventory-service-application-on-bluemix-container)**. 
    ```
    # cf ic inspect mysql | grep -i ipaddress
                "IPAddress": "172.29.0.240",
                        "IPAddress": "172.29.0.240"
    ```

Inventory database is now setup in IBM Bluemix Container.


### Deploy Elasticsearch on Bluemix container
1. **Log in to your Bluemix account**.
    ```
    # cf login -a <bluemix-api-endpoint> -u <your-bluemix-user-id>
    ```

2. **Set target to use your Bluemix Org and Space**.
    ```
    # cf target -o <your-bluemix-org> -s <your-bluemix-space>
    ```

3. **Log in to IBM Containers plugin**.
    ```
    # cf ic login
    ```

4. **Check that your organization has set a namespace**.
    ```
    # cf ic namespace get
    ```

5. **If there is no namespace set for your organization, then set a namespace**.
    ```
    # cf ic namespace get
    ```

6. **Pull elasticsearch docker image**.
    ```
    docker pull elasticsearch
    ```

7. **Tag image**.
    ```
    docker tag elasticsearch registry.ng.bluemix.net/$(cf ic namespace get)/elasticsearch
    ```

8. **Push image to Bluemix Docker image registry**.
    ```
    docker push registry.ng.bluemix.net/$(cf ic namespace get)/elasticsearch
    ```

9. **Create single Elasticsearch container**.
    ```
    cf ic run -m 4096 --name elasticsearch -p 9200:9200 registry.ng.bluemix.net/$(cf ic namespace get)/elasticsearch
    ```

10. **Verify container is `Running`**.
    ```
    # cf ic ps | grep elasticsearch
    a9a5a80c-bff        registry.ng.bluemix.net/chrisking/elasticsearch:latest    ""                  About an hour ago   Running             169.46.17.110:9200->9200/tcp   elasticsearch-container
    ```

12. **Get container Private IP Address**. 
    ```
    # cf ic inspect elasticsearch | grep -i ipaddress
                "IPAddress": "172.29.0.240",
                        "IPAddress": "172.29.0.240"
    ```

13. **Use `http(s)://CONTAINER_IP_ADDRESS:9200` as your `elasticsearch.url` when deploying `Catalog` and `Inventory`**.

### Deploy Inventory Service application on Bluemix container
In this section you will deploy both the database server and the Spring Boot application to run in IBM Bluemix containers.

1. **Change to `inventory` directory**.
    ```
    cd inventory
    ```

2. **Log in to your Bluemix account**.
    ```
    # cf login -a <bluemix-api-endpoint> -u <your-bluemix-user-id>
    ```

3. **Set target to use your Bluemix Org and Space**.
    ```
    # cf target -o <your-bluemix-org> -s <your-bluemix-space>
    ```

4. **Log in to IBM Containers plugin**.
    ```
    # cf ic login
    ```

5. **Build container image**
    ```
    # ./gradlew build -x test
    # ./gradlew docker
    # cd docker
    # docker build -t cloudnative/inventoryservice .
    ```

6. **Tag and push the local docker image to bluemix private registry**.
    ```
    # docker tag cloudnative/inventoryservice registry.ng.bluemix.net/$(cf ic namespace get)/inventoryservice:cloudnative
    # docker push registry.ng.bluemix.net/$(cf ic namespace get)/inventoryservice:cloudnative
    ```

7. **If not already done, [Deploy `MySQL` on IBM Bluemix container](#deploy-mysql-on-bluemix-container)**.
  - Obtain the MySQL `Container Private IP Address`, `database username`, and `database password` from link above and have them ready for deployment in `Step 10`.

8. **If not already done, [Deploy `Elasticsearch` on Bluemix container](#deploy-elasticsearch-on-bluemix-container)**.
  - Obtain the Elasticsearch `Container Private IP Address` from link above and have it ready for deployment in `Step 10`.
 
9. **If not already done, [Provision `Message Hub` service instance](#message-hub)**.
  - After provisioning, go to instance `Service Credentials` tab on Bluemix, then press `View credentials`.
  - Obtain the Message HUB `user`, `password`, `api_key`, `kafka_rest_url`, and `kafka_brokers_sasl` from link above and have them ready for deployment in `Step 10`.

10. **Start the application in IBM Bluemix container**.
  ```
  # cf ic group create -p 8080 -m 1024 --min 1 --auto --name micro-inventory-group \
    -e "spring.datasource.url=jdbc:mysql://{mysql_ip}:3306/inventorydb" \
    -e "spring.datasource.username={mysql_user}" \
    -e "spring.datasource.password={mysql_password}" \
    -e "elasticsearch.url={es_url}" \
    -e "message_hub.user={mh_user}" \
    -e "message_hub.password={mh_password}" \
    -e "message_hub.api_key={mh_api_key}" \
    -e "message_hub.kafka_rest_url={mh_kafka_rest_url}" \
    -e "message_hub.kafka_brokers_sasl[0]={mh_kafka_broker_0}" \
    -e "message_hub.kafka_brokers_sasl[1]={mh_kafka_broker_1}" \
    -e "message_hub.kafka_brokers_sasl[2]={mh_kafka_broker_2}" \
    -e "message_hub.kafka_brokers_sasl[3]={mh_kafka_broker_3}" \
    -e "message_hub.kafka_brokers_sasl[4]={mh_kafka_broker_4}" \
    -n inventoryservice \
    -d mybluemix.net registry.ng.bluemix.net/$(cf ic namespace get)/inventoryservice:cloudnative
  ```

  - Replace `{mysql_ip}` with the MySQL Bluemix container instance IP address.
  - Replace `{mysql_user}` with database username.
  - Replace `{mysql_password}` with database user password.
  - Replace `{es_url}` with the Elasticsearch Bluemix container private ip address.
  - Replace `{mh_user}` with Message Hub `user`.
  - Replace `{mh_password}` with Message Hub `password`.
  - Replace `{mh_api_key}` with Message Hub `api_key`.
  - Replace `{mh_kafka_rest_url}` with Message Hub `kafka_rest_url`.
  - Replace all the `{mh_kafka_broker[x]}` with all the URLs listed in Message Hub `kafka_brokers_sasl`.

11. **Optional. If using Compose for [Elasticsearch](#deploy-elasticsearch-on-bluemix-using-compose) and/or [MySQL](#deploy-inventory-database-on-bluemix-using-compose), the command will look like this**:
  ```
  # cf ic group create -p 8080 -m 1024 --min 1 --auto --name micro-inventory-group \
    -e "spring.datasource.url=jdbc:mysql://{mysql_ip}:{msql_port}/inventorydb" \
    -e "spring.datasource.username={mysql_user}" \
    -e "spring.datasource.password={mysql_password}" \
    -e "elasticsearch.url={es_url}" \
    -e "elasticsearch.user={es_user}" \
    -e "elasticsearch.password={es_password}" \
    -e "message_hub.user={mh_user}" \
    -e "message_hub.password={mh_password}" \
    -e "message_hub.api_key={mh_api_key}" \
    -e "message_hub.kafka_rest_url={mh_kafka_rest_url}" \
    -e "message_hub.kafka_brokers_sasl[0]={mh_kafka_broker_0}" \
    -e "message_hub.kafka_brokers_sasl[1]={mh_kafka_broker_1}" \
    -e "message_hub.kafka_brokers_sasl[2]={mh_kafka_broker_2}" \
    -e "message_hub.kafka_brokers_sasl[3]={mh_kafka_broker_3}" \
    -e "message_hub.kafka_brokers_sasl[4]={mh_kafka_broker_4}" \
    -n inventoryservice \
    -d mybluemix.net registry.ng.bluemix.net/$(cf ic namespace get)/inventoryservice:cloudnative
  ```

  - Replace `{mysql_ip}` with the MySQL Compose host.
  - Replace `{mysql_port}` with the MySQL Compose port.
  - Replace `{mysql_user}` with MySQL Compose user.
  - Replace `{mysql_password}` with MySQL Compose user password.
  - Replace `{es_url}` with the Elasticsearch Compose URL in the format of `https://host:port`.
  - Replace `{es_user}` with the Elasticsearch Compose username`.
  - Replace `{es_password}` with the Elasticsearch Compose password.
  - Replace `{mh_user}` with Message Hub `user`.
  - Replace `{mh_password}` with Message Hub `password`.
  - Replace `{mh_api_key}` with Message Hub `api_key`.
  - Replace `{mh_kafka_rest_url}` with Message Hub `kafka_rest_url`.
  - Replace all the `{mh_kafka_broker[x]}` with all the URLs listed in Message Hub `kafka_brokers_sasl`.

12. **Validate. You should get a list of all inventory items**.
    ```
    # curl http://{container-group-route-name}/micro/inventory
    ```

13. **Unmap public route**.
    ```
    # cf ic route unmap -n inventoryservice -d mybluemix.net micro-inventory-group
    ```


### Deploy Catalog Service application on Bluemix container
In this section you will deploy the `Catalog` Spring Boot application in IBM Bluemix containers.

1. **Change to `catalog` directory**.
    ```
    cd catalog
    ```

2. **Log in to your Bluemix account**.
    ```
    # cf login -a <bluemix-api-endpoint> -u <your-bluemix-user-id>
    ```

3. **Set target to use your Bluemix Org and Space**.
    ```
    # cf target -o <your-bluemix-org> -s <your-bluemix-space>
    ```

4. **Log in to IBM Containers plugin**.
    ```
    # cf ic login
    ```

5. **Build container image**
    ```
    # ./gradlew build -x test
    # ./gradlew docker
    # cd docker
    # docker build -t cloudnative/catalogservice .
    ```

6. **Tag and push the local docker image to bluemix private registry**.
    ```
    # docker tag cloudnative/catalogservice registry.ng.bluemix.net/$(cf ic namespace get)/catalogservice:cloudnative
    # docker push registry.ng.bluemix.net/$(cf ic namespace get)/catalogservice:cloudnative
    ```

7. **If not already done, [Deploy Elasticsearch on Bluemix container](#deploy-elasticsearch-on-bluemix-container)**.
  - Obtain the Elasticsearch `Container Private IP Address` from link above and have it ready for deployment in `Step 8`.

8. **Start the application in IBM Bluemix container**.

  ```
  # cf ic group create -p 8080 -m 1024 --min 1 --auto --name micro-catalog-group \
    -e "eureka.client.fetchRegistry=true" \
    -e "eureka.client.registerWithEureka=true" \
    -e "eureka.client.serviceUrl.defaultZone=http://netflix-eureka-$(cf ic namespace get).mybluemix.net/eureka/" \
    -e "elasticsearch.url={es_url}" \
    -n catalogservice \
    -d mybluemix.net registry.ng.bluemix.net/$(cf ic namespace get)/catalogservice:cloudnative
  ```

  - Replace `{es_url}` with the Elasticsearch Bluemix container private ip address.

9. **Optional. If using Compose for [Elasticsearch](#deploy-elasticsearch-on-bluemix-using-compose), the command will look like this**:
  ```
  # cf ic group create -p 8080 -m 1024 --min 1 --auto --name micro-catalog-group \
    -e "eureka.client.fetchRegistry=true" \
    -e "eureka.client.registerWithEureka=true" \
    -e "eureka.client.serviceUrl.defaultZone=http://netflix-eureka-$(cf ic namespace get).mybluemix.net/eureka/" \
    -e "elasticsearch.url={es_url}" \
    -e "elasticsearch.user={es_user}" \
    -e "elasticsearch.password={es_password}" \
    -n catalogservice \
    -d mybluemix.net registry.ng.bluemix.net/$(cf ic namespace get)/catalogservice:cloudnative
  ```

  - Replace `{es_url}` with the Elasticsearch Compose URL in the format of `https://host:port`.
  - Replace `{es_user}` with the Elasticsearch Compose username`.
  - Replace `{es_password}` with the Elasticsearch Compose password.

10. **Validate. You should get a list of all catalog items**.
    ```
    # curl http://{container-group-route-name}/micro/items
    ```

11. **Unmap public route**.
    ```
    # cf ic route unmap -n catalogservice -d mybluemix.net micro-catalog-group
    ```

## Using Compose for Production Databases
Compose is an IBM service that provides production ready Cloud Hosted Databases. In this section you will learn how to deploy both a MySQL and Elasticsearch databases using Compose from Bluemix Catalog.

### Deploy MySQL on Bluemix using Compose
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
5. Keep those credential handy and feel free to use them when [deploying Inventory Service application on Bluemix container](#deploy-inventory-service-application-on-bluemix-container).
6. **Create `items` table and load sample data. You should see message _Data loaded to inventorydb.items._**
    ```
    # cd mysql/scripts
    # bash load-data-compose.sh {USER} {PASSWORD} {HOST} {PORT}
    ```
 
  - Replace `{USER}` with MySQL user.
  - Replace `{PASSWORD}` with MySQL password.
  - Replace `{HOST}` with MySQL host.
  - Replace `{PORT}` with MySQL port.

Inventory database is now setup in Compose.

### Deploy Elasticsearch on Bluemix using Compose
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
5. Keep those credential handy and feel free to use them when provisioning [Inventory](#deploy-inventory-service-application-on-bluemix-container) and [Catalog](#deploy-catalog-service-application-on-bluemix-container) services on Bluemix Containers.

Inventory database is now setup in Compose.
