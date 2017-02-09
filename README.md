######refarch-cloudnative-micro-inventory

###Spring Boot Netflix OSS app Integration with MySQL Database Server

*This project is part of the 'IBM Cloud Native Reference Architecture' suite, available at
https://github.com/ibm-solution-engineering/refarch-cloudnative*


##Table of Contents
- [Introduction](#introduction)
  - [APIs](#apis)
- [Pre-requisites](#pre-requisites)
  - [Message Hub](#message-hub)
  - [Elasticsearch](#elasticsearch)
    - [Deploy Elasticsearch locally](#deploy-elasticsearch-locally)
    - [Deploy Elasticsearch on local docker container](#deploy-elasticsearch-on-local-docker-container)
    - [Deploy Elasticsearch on Bluemix container](#deploy-elasticsearch-on-bluemix-container) 
  - [MySQL](#mysql)
    - [Deploy MySQL on local docker container](#deploy-mysql-on-local-docker-container)
    - [Deploy Inventory Database in IBM Bluemix container](#deploy-inventory-database-in-ibm-bluemix-container)
- [Inventory Microservice](#inventory-microservice)
  - Local
  - Container
  - Bluemix
- Catalog
  - Local
  - Container
  - Bluemix

##Introduction

This project is built to demonstrate how to build a Spring Boot application to use a MySQL database using Spring Data JPA.
 - Leverage Spring Boot framework to build a Microservices application.
 - Use Spring Data JPA to persist data to MySQL database.
 - Integrate with Netflix Eureka framework.
 - Deployment option for IBM Bluemix Container runtime.

**Insert diagram here**

Clone git repository before getting started.
    ```
    git clone http://github.com/refarch-cloudnative-micro-inventory.git
    cd refarch-cloudnative-micro-inventory
    ```


###APIs
You can use cURL or Chrome POSTMAN to send get/post/put/delete requests to the application.
- Get all items in inventory
http://<hostname>/micro/items

- Get item by id
`http://<hostname>/micro/items/{id}`

- Example curl command to get al items
  ```
  curl -X GET "http://localhost:8081/items/inventory"
  ```

##Pre-requisites:
- You need a docker machine running on localhost to host container(s). [Click for instructions](https://docs.docker.com/engine/installation/).
- You need to [Provision `MessageHub`](#message-hub) service instance.
- You need to deploy an elasticsearch container. Instructions below.

###Message Hub
- [Provision](https://console.ng.bluemix.net/catalog/services/message-hub) and instance of Message Hub into your bluemix space.
  - Select name for your instance.
  - Click the `Create` button.
- Once provisioned, obtain `Message Hub` service credentials.
  - Click on `Service Credentials` tab.
  - Then click on the `View Credentials` dropdown next to the credentials.
- You will need the following:
  - **kafka_rest_url:** Needed to query and create `topics`.
  - **api_key:** Needed to use the Message Hub REST API.
  - **user:** Message Hub user.
  - **password:** Message Hub password.
  - **kafka_brokers_sasl:** Message Hub kafka brokers, which are in charge of receiving and sending messages for specific topics.
- Keep those credential handy as they will be needed later when [provisioning Inventory Service](#) **dead link here**

###Elasticsearch
Elasticsearch is used as the `Catalog` microservice's data source.

####Deploy Elasticsearch locally
1. [Install Elasticsearch locally](https://www.elastic.co/guide/en/elasticsearch/reference/current/_installation.html)
2. Run Elasicsearch
  ```
  cd elasticsearch_installation_path/bin
  ./elasticsearch
  ```

3. Validate.
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

4. Use `http://localhost:9200` as your `elasticsearch.url` when deploying `Catalog` and `Inventory`.

####Deploy Elasticsearch on local docker container
1. Pull elasticsearch docker image
    ```
    docker pull elasticsearch
    ```

2. Run docker container locally
    ```
    docker run -d -p 9200:9200 elasticsearch
    ```

3. Validate.
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

4. Use `http://localhost:9200` as your `elasticsearch.url` when deploying `Catalog` and `Inventory`.

####Deploy Elasticsearch on Bluemix container
1. Pull elasticsearch docker image
    ```
    docker pull elasticsearch
    ```

2. Get the `elasticsearch` docker image id and then copy to clipboard
    ```
    docker images
    ```

3. Tag image
    ```
    docker tag elasticsearch registry.ng.bluemix.net/$(cf ic namespace get)/elasticsearch
    ```

4. Push image to Bluemix Docker image registry
    ```
    docker push registry.ng.bluemix.net/$(cf ic namespace get)/elasticsearch
    ```

5. Or create single Elasticsearch container
    ```
    cf ic run -d -p 9200:9200 --name elasticsearch-container registry.ng.bluemix.net/$(cf ic namespace get)/elasticsearch
    ```

6. Check for installation
    ```
    cf ic ps | grep -i elasticsearch-container
    ```
 
7. Get container Private IP Address. 
    ```
    # cf ic inspect elasticsearch-container | grep -i ipaddress
                "IPAddress": "172.29.0.240",
                        "IPAddress": "172.29.0.240"
    ```

8. Use `http(s)://CONTAINER_IP_ADDRESS:9200` as your `elasticsearch.url` when deploying `Catalog` and `Inventory`.


###MySQL
Elasticsearch is used as the `Catalog` microservice's data source.

#### Deploy MySQL on local docker container
1. Change to the mysql directory.
    ```
    # cd mysql
    ```

2. Create MySQL container with database `inventorydb`. This database can be connected at `<docker-host-ipaddr/hostname>:3306` as `dbuser` using `password`.
    ```
    Linux/macOS:
    # docker run --name mysql -v $PWD/scripts:/home/scripts -p 3306:3306 -e MYSQL_ROOT_PASSWORD=admin123 -e MYSQL_USER=dbuser -e MYSQL_PASSWORD=password -e MYSQL_DATABASE=inventorydb -w /home/scripts -d mysql:latest
    ```
    ```
    Windows:
    # docker run --name mysql -v %CD%\scripts:/home/scripts -p 3306:3306 -e MYSQL_ROOT_PASSWORD=admin123 -e MYSQL_USER=dbuser -e MYSQL_PASSWORD=password -e MYSQL_DATABASE=inventorydb -w /home/scripts -d mysql:latest
    ```


3. Create `items` table and load sample data.
    ```
    # docker exec -it mysql sh load-data.sh
    ```

4. Verify, there should be 12 rows in the table.
    ```
    # docker exec -it mysql bash
    # mysql -u ${MYSQL_USER} -p${MYSQL_PASSWORD} ${MYSQL_DATABASE}
    mysql> select * from items;
    mysql> quit
    # exit
    ```

5. Get container Private IP Address. This will be used when running [Running inventory from local docker container](#run-inventory-service-application-on-local-docker-container) 
    ```
    # cf ic inspect elasticsearch-container | grep -i ipaddress
                "IPAddress": "172.29.0.240",
                        "IPAddress": "172.29.0.240"
    ```


Inventory database is now setup in local container.

####Deploy Inventory Database in IBM Bluemix container

1. Change to the mysql directory.
    ```
    # cd mysql
    ```

2. Build docker image using the Dockerfile from repo.
    ```
    # docker build -t cloudnative/mysql .
    ```

3. Log in to your Bluemix account.
    ```
    # cf login -a <bluemix-api-endpoint> -u <your-bluemix-user-id>
    ```

4. Set target to use your Bluemix Org and Space.
    ```
    # cf target -o <your-bluemix-org> -s <your-bluemix-space>
    ```

5. Log in to IBM Containers plugin.
    ```
    # cf ic login
    ```

6. Check that your organization has set a namespace.
    ```
    # cf ic namespace get
    ```

7. If there is no namespace set for your , then set a namespace.
    ```
    # cf ic namespace get
    ```

8. Tag and push mysql database server image to your Bluemix private registry namespace.
    ```
    # docker tag cloudnative/mysql registry.ng.bluemix.net/$(cf ic namespace get)/mysql:cloudnative
    # docker push registry.ng.bluemix.net/$(cf ic namespace get)/mysql:cloudnative
    ```

9. Create MySQL container with database `inventorydb`. This database can be connected at `<docker-host-ipaddr/hostname>:3306` as `dbuser` using `Pass4dbUs3R`.

    _It is recommended to change the default passwords used here._
    ```
    # cf ic run -m 512 --name mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=Pass4Admin123 -e MYSQL_USER=dbuser -e MYSQL_PASSWORD=Pass4dbUs3R -e MYSQL_DATABASE=inventorydb registry.ng.bluemix.net/$(cf ic namespace get)/mysql:cloudnative
    ```

10. Create `items` table and load sample data. You should see message _Data loaded to inventorydb.items._
    ```
    # cf ic exec -it mysql sh load-data.sh
    ```

11. Verify, there should be 12 rows in the table.
    ```
    # cf ic exec -it mysql bash
    # mysql -u ${MYSQL_USER} -p${MYSQL_PASSWORD} ${MYSQL_DATABASE}
    mysql> select * from items;
    mysql> quit
    # exit
    ```

Inventory database is now setup in IBM Bluemix Container.



##Inventory Microservice
Inventory Microservice




####Build the application
1. Change to `inventory` directory.
    ```
    cd inventory
    ```

2. Build the application.
    ```
    # ./gradlew build -x test
    ```

####Run Inventory Service application on localhost
In this section you will deploy the Spring Boot application to run on your localhost.

1. [Deploy `MySQL` on local docker container](#deploy-mysql-on-local-docker-container).
  - Open `src/main/resources/application.yml`, go to `datasource` section.
  - Type `url` as `jdbc:mysql://127.0.0.1/inventorydb`, where `127.0.0.1` means `localhost`
  - Make sure the `user`, `password`, and `port` match those of local container.

2. [Deploy `Elasticsearch` locally](#deploy-elasticsearch-locally).
  - Open `src/main/resources/application.yml`, go to `elasticsearch` section.
  - Type `http://localhost:9200` on the `url` field.

3. [Provision `Message Hub` service instance](#message-hub).
  - After provisioning, go to instance `Service Credentials` tab on Bluemix, then press `View credentials`.
  - Open `src/main/resources/application.yml`, go to `message_hub` section, then copy and paste required `message_hub` fields using credentials from above.

4. Run the application on localhost.
    ```
    # java -jar build/libs/micro-inventory-0.0.1.jar
    ```

5. Validate. You should get a list of all inventory items
    ```
    # curl http://localhost:8080/micro/inventory
    ```

####Run Inventory Service application on local docker container
In this section you will deploy the Spring Boot application to run in a local docker container.

1. **Build container image**
    ```
    # ./gradlew docker
    # cd docker
    # docker build -t cloudnative/inventoryservice .
    ```

2. **If not already done, [Deploy MySQL on local docker container](#deploy-mysql-on-local-docker-container)**.
  - Obtain the MySQL `Container Private IP Address`, `database username`, and `database password` from link above and have them ready for deployment in `Step 5`.

3. **If not already done, [Deploy Elasticsearch on local docker container](#deploy-elasticsearch-on-local-docker-container)**.
  - Obtain the Elasticsearch `Container Private IP Address` from link above and have it ready for deployment in `Step 5`.
 
4. **If not already done, [Provision `Message Hub` service instance](#message-hub)**.
  - After provisioning, go to instance `Service Credentials` tab on Bluemix, then press `View credentials`.
  - Obtain the Message HUB `user`, `password`, `api_key`, `kafka_rest_url`, and `kafka_brokers_sasl` from link above and have them ready for deployment in `Step 5`.

5. **Start the application in docker container**.
  - **`MySQL`**:
    - Replace `{mysql_docker_ip}` with the mysql container instance IP address.
    - Replace `{mysql_user}` with database username.
    - Replace `{mysql_password}` with database user password.

  - **`Elasticsearch`**:
    - Replace `{es_url}` with Elasticsearch container private ip address.

  - **`Message Hub`**:
    - Replace `{mh_user}` with Message Hub `user`.
    - Replace `{mh_password}` with Message Hub `password`.
    - Replace `{mh_api_key}` with Message Hub `api_key`.
    - Replace `{mh_kafka_rest_url}` with Message Hub `kafka_rest_url`.
    - Replace all the `{mh_kafka_broker[x]}` with all the URLs listed in Message Hub `kafka_brokers_sasl`.

  ```
  # docker run -d -p 8080:8080 --name inventoryservice \
    -e "spring.datasource.url=jdbc:mysql://{mysql_docker_ip}:3306/inventorydb" \
    -e "spring.datasource.username=${mysql_user}" \
    -e "spring.datasource.password=${mysql_password}" \
    -e "elasticsearch.url=${es_url}" \
    -e "message_hub.user=${mh_user}" \
    -e "message_hub.password=${mh_password}" \
    -e "message_hub.api_key=${mh_api_key}" \
    -e "message_hub.kafka_rest_url=${mh_kafka_rest_url}" \
    -e "message_hub.kafka_brokers_sasl[0]=${mh_kafka_broker_0}" \
    -e "message_hub.kafka_brokers_sasl[1]=${mh_kafka_broker_1}" \
    -e "message_hub.kafka_brokers_sasl[2]=${mh_kafka_broker_2}" \
    -e "message_hub.kafka_brokers_sasl[3]=${mh_kafka_broker_3}" \
    -e "message_hub.kafka_brokers_sasl[4]=${mh_kafka_broker_4}" \
    cloudnative/inventoryservice
  ```

6. Validate. You should get a list of all inventory items
    ```
    # curl http://localhost:8080/micro/inventory
    ```

####Deploy Inventory Service application to IBM Bluemix container
In this section you will deploy both the database server and the Spring Boot application to run in IBM Bluemix containers.

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

4. **Tag and push the local docker image to bluemix private registry**.
    ```
    # docker tag cloudnative/inventoryservice registry.ng.bluemix.net/$(cf ic namespace get)/inventoryservice:cloudnative
    # docker push registry.ng.bluemix.net/$(cf ic namespace get)/inventoryservice:cloudnative
    ```

5. **If not already done, [Deploy Inventory Database in IBM Bluemix container](#deploy-inventory-database-in-ibm-bluemix-container)**.
  - Obtain the MySQL `Container Private IP Address`, `database username`, and `database password` from link above and have them ready for deployment in `Step 9`.

6. **If not already done, [Deploy Elasticsearch on Bluemix container](#deploy-elasticsearch-on-bluemix-container)**.
  - Obtain the Elasticsearch `Container Private IP Address` from link above and have it ready for deployment in `Step 9`.
 
7. **If not already done, [Provision `Message Hub` service instance](#message-hub)**.
  - After provisioning, go to instance `Service Credentials` tab on Bluemix, then press `View credentials`.
  - Obtain the Message HUB `user`, `password`, `api_key`, `kafka_rest_url`, and `kafka_brokers_sasl` from link above and have them ready for deployment in `Step 9`.

8. Deploy `ElasticSearch` container on Bluemix and get the `Elasticsearch connection string`.

9. Start the application in IBM Bluemix container. 
  - **`MySQL`**:
    - Replace `{mysql_container_ip}` with the MySQL Bluemix container instance IP address.
    - Replace `{mysql_user}` with database username.
    - Replace `{mysql_password}` with database user password.

  - **`Elasticsearch`**:
    - Replace `{es_url}` with the Elasticsearch Bluemix container private ip address.

  - **`Message Hub`**:
    - Replace `{mh_user}` with Message Hub `user`.
    - Replace `{mh_password}` with Message Hub `password`.
    - Replace `{mh_api_key}` with Message Hub `api_key`.
    - Replace `{mh_kafka_rest_url}` with Message Hub `kafka_rest_url`.
    - Replace all the `{mh_kafka_broker[x]}` with all the URLs listed in Message Hub `kafka_brokers_sasl`.
  ```
  # cf ic group create -p 8080 -m 128 --min 1 --auto --name micro-inventory-group \
    -e "spring.datasource.url=jdbc:mysql://{mysql_docker_ip}:3306/inventorydb" \
    -e "spring.datasource.username=${mysql_user}" \
    -e "spring.datasource.password=${mysql_password}" \
    -e "elasticsearch.url=${es_url}" \
    -e "message_hub.user=${mh_user}" \
    -e "message_hub.password=${mh_password}" \
    -e "message_hub.api_key=${mh_api_key}" \
    -e "message_hub.kafka_rest_url=${mh_kafka_rest_url}" \
    -e "message_hub.kafka_brokers_sasl[0]=${mh_kafka_broker_0}" \
    -e "message_hub.kafka_brokers_sasl[1]=${mh_kafka_broker_1}" \
    -e "message_hub.kafka_brokers_sasl[2]=${mh_kafka_broker_2}" \
    -e "message_hub.kafka_brokers_sasl[3]=${mh_kafka_broker_3}" \
    -e "message_hub.kafka_brokers_sasl[4]=${mh_kafka_broker_4}" \
    -n inventoryservice \
    -d mybluemix.net registry.ng.bluemix.net/$(cf ic namespace get)/inventoryservice:cloudnative
  ```

10. Validate. You should get a list of all inventory items
    ```
    # curl http://{container-group-route-name}/micro/inventory
    ```

11. **Unmap public route**.
    ```
    # cf ic route unmap -n inventoryservice -d mybluemix.net micro-inventory-group
    ```
