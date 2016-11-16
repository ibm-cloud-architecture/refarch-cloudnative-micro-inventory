######refarch-cloudnative-micro-inventory

###Spring Boot Netflix OSS app Integration with MySQL Database Server

*This project is part of the 'IBM Cloud Native Reference Architecture' suite, available at
https://github.com/ibm-solution-engineering/refarch-cloudnative*

####Introduction

This project is built to demonstrate how to build a Spring Boot application to use a MySQL database using Spring Data JPA.
 - Leverage Spring Boot framework to build a Microservices application.
 - Use Spring Data JPA to persist data to MySQL database.
 - Integrate with Netflix Eureka framework.
 - Deployment option for IBM Bluemix Container runtime.

####APIs in this application
You can use cURL or Chrome POSTMAN to send get/post/put/delete requests to the application.
- Get all items in inventory
http://<hostname>/micro/inventory

- Get item by id
`http://<hostname>/micro/inventory/{id}`

- Get items by name or names containing substring
`http://<hostname>/micro/inventory/name/{name}`

- Get items by price less than or equal to
`http://<hostname>/micro/inventory/price/{price}`

- Add new item to inventory - `POST` json payload
`http://<hostname>/micro/inventory`

- Update existing item in inventory - `PUT` json payload
`http://<hostname>/micro/inventory/update/{id}`

- Delete item from inventory - Send `DELETE` request
`http://<hostname>/micro/inventory/delete/{id}`

- Example curl command to add item
    ```
    curl -X POST -H "Content-Type: application/json" -d '{
    "name": "Credit Card Reader",
    "description": "For use with IBM Point of Sale systems",
    "img": "CC-Reader.jpg",
    "imgAlt": "Credit Card Reader",
    "price": "1599.99"
    }' "http://<hostname>/micro/inventory"
    ```

####Pre-requisite:
- You need a docker machine running on localhost to host container(s). [Click for instructions](https://docs.docker.com/machine/get-started/).

####Build the application
1. Clone git repository.
    ```
    git clone http://github.com/refarch-cloudnative-micro-inventory.git
    cd refarch-cloudnative-micro-inventory
    ```

2. Build the application.
    ```
    # ./gradlew build
    ```

####Run Inventory Service application on localhost
In this section you will deploy the Spring Boot application to run on your localhost. The database server will be setup to run as a container on the local machine.

1. [Setup MySQL database `inventorydb` on local docker container](https://github.com/ibm-cloud-architecture/refarch-cloudnative-mysql#setup-inventory-database-on-local-mysql-container).

2. Run the application on localhost.
    ```
    # java -jar build/libs/micro-inventory-0.0.1.jar
    ```

3. Validate.
    ```
    # curl http://localhost:8080/micro/inventory/13412
    {"id":13412,"name":"Selectric Typewriter","description":"Unveiled in 1961, the revolutionary Selectric typewriter eliminated the need for conventional type bars and movable carriages by using an innovative typing element on a head-and-rocker assembly, which, in turn, was mounted on a small carrier to move from left to right while typing.","price":2199,"img":"api/image/selectric.jpg","imgAlt":"Selectric Typewriter"}
    ```

####Run Inventory Service application on local docker container
In this section you will deploy the Spring Boot application to run in a local docker container. The database server will also be setup to run as a container on the local machine.

1. Build container image
    ```
    # ./gradlew docker
    # cd docker
    # docker build -t cloudnative/inventoryservice .
    ```

3. If not already done, [setup MySQL database `inventorydb` on local docker container](https://github.com/ibm-cloud-architecture/refarch-cloudnative-mysql#setup-inventory-database-on-local-mysql-container).

3. Start the application in docker container.  

   Replace `{dbuser}` with database user name and `{password}` with database user password.  
   The `{mysql-docker-ip}` is the mysql container instance IP address. For users running on Docker version prior to v1.12, it is the IP address of the docker-machine. For Docker 1.12 and later, you need to replace the {mysql-docker-ip} with the value from the result of executing 'docker inspect mysql'. You should look the Networking section, find the **IPAddress**.   

    ```
    # docker run -d -p 8080:8080 --name inventoryservice -e "spring.datasource.url=jdbc:mysql://{mysql-docker-ip}:3306/inventorydb" -e "spring.datasource.username={dbuser}" -e "spring.datasource.password={password}" cloudnative/inventoryservice
    ```

4. Validate.  
    ```
    # curl http://{docker-host}:8080/micro/inventory/13412
    {"id":13412,"name":"Selectric Typewriter","description":"Unveiled in 1961, the revolutionary Selectric typewriter eliminated the need for conventional type bars and movable carriages by using an innovative typing element on a head-and-rocker assembly, which, in turn, was mounted on a small carrier to move from left to right while typing.","price":2199,"img":"api/image/selectric.jpg","imgAlt":"Selectric Typewriter"}
    ```

####Deploy Inventory Service application to IBM Bluemix container
In this section you will deploy both the database server and the Spring Boot application to run in IBM Bluemix containers.

1. Log in to your Bluemix account.
    ```
    # cf login -a <bluemix-api-endpoint> -u <your-bluemix-user-id>
    ```

2. Set target to use your Bluemix Org and Space.
    ```
    # cf target -o <your-bluemix-org> -s <your-bluemix-space>
    ```

3. Log in to IBM Containers plugin.
    ```
    # cf ic login
    ```

4. Tag and push the local docker image to bluemix private registry.
    ```
    # docker tag cloudnative/inventoryservice registry.ng.bluemix.net/$(cf ic namespace get)/inventoryservice:cloudnative
    # docker push registry.ng.bluemix.net/$(cf ic namespace get)/inventoryservice:cloudnative
    ```

5. [Setup MySQL `inventorydb` database in IBM Bluemix container](https://github.com/ibm-cloud-architecture/refarch-cloudnative-mysql#setup-inventory-database-in-bluemix-container-runtime).

6. Get private IP address of the database container.

    ```
    # cf ic inspect mysql | grep -i ipaddress
    ```

7. Start the application in IBM Bluemix container. Replace `{ipaddr-db-container}` with private IP address of the database container, `{dbuser}` with database user name and `{password}` with database user password.
    ```
    # cf ic group create -p 8080 -m 128 --min 1 --auto --name micro-inventory-group -e "spring.datasource.url=jdbc:mysql://{ipaddr-db-container}:3306/inventorydb" -e "spring.datasource.username={dbuser}" -e "spring.datasource.password={password}" -e eureka.client.fetchRegistry=true -e eureka.client.registerWithEureka=true -e eureka.client.serviceUrl.defaultZone=http://netflix-eureka-$(cf ic namespace get).mybluemix.net/eureka/ -n inventoryservice -d mybluemix.net registry.ng.bluemix.net/$(cf ic namespace get)/inventoryservice:cloudnative
    ```

8. Validate.
    ```
    # curl http://{container-group-route-name}/micro/inventory/13412
    {"id":13412,"name":"Selectric Typewriter","description":"Unveiled in 1961, the revolutionary Selectric typewriter eliminated the need for conventional type bars and movable carriages by using an innovative typing element on a head-and-rocker assembly, which, in turn, was mounted on a small carrier to move from left to right while typing.","price":2199,"img":"api/image/selectric.jpg","imgAlt":"Selectric Typewriter"}
    ```

9. Unmap public route.
    ```
    # cf ic route unmap -n inventoryservice -d mybluemix.net micro-inventory-group
    ```
