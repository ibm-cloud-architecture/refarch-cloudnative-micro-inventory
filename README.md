###### refarch-cloudnative-micro-inventory

## Spring Boot Netflix OSS app Integration with MySQL Database Server

*This project is part of the 'IBM Cloud Native Reference Architecture' suite, available at
https://github.com/ibm-solution-engineering/refarch-cloudnative*

### Introduction

This project is built to demonstrate how to build a Spring Boot Microservices application to access MySQL database. It enables data management using Spring Data JPA.

 - Leverage Spring Boot framework to build Microservices application.
 - Use Spring Data JPA to access Cloudant database.
 - Integrate with Netflix Eureka framework. *TODO*
 - Deployment option for IBM Bluemix Cloud Foundry and Container runtimes.


### Run the application locally:

 1. [Create MySQL inventorydb database](https://github.com/ibm-cloud-architecture/refarch-cloudnative-mysql)

 
 2. Build the application:

    ```
    $ ./gradlew build
    ```

 3. Run the app:

    ```
    $ java -jar build/libs/micro-inventory-0.0.1.jar
    ```

 4. Validate the application

    - Get all items in inventory
    [http://localhost:8080/micro/inventory](http://localhost:8080/micro/inventory)

    - Get item by id
    `http://localhost:8080/micro/inventory/{id}`

    - Get items by name or names containing substring
    `http://localhost:8080/micro/inventory/name/{name}`

    - Get items by price less than or equal to
    `http://localhost:8080/micro/inventory/price/{price}`

    You can use cURL or use Chrome POSTMAN to send insert/update/delete requests to the database.

    - Add new item to inventory - `POST` json payload
    `http://localhost:8080/micro/inventory`

    - Update existing item in inventory - `PUT` json payload
    `http://localhost:8080/micro/inventory/update/{id}`

    - Delete item from inventory - Send `DELETE` request
    `http://localhost:8080/micro/inventory/delete/{id}`

    Example curl command
     ```
     curl -X POST -H "Content-Type: application/json" -d '{
     "name": "Credit Card Reader",
     "description": "For use with IBM Point of Sale systems",
     "img": "CC-Reader.jpg",
     "imgAlt": "Credit Card Reader",
     "price": "1599.99"
     }' "http://localhost:8080/micro/inventory/create"
    ```


### Deploy to local Docker environment

Ensure that you have local docker environment setup properly. The solution requires docker-compose. The script is validated with docker version 1.11.x

1. Copy application binary to docker folder:
    ```
    $ ./gradlew docker
    ```

2. Build the docker image:
    ```
    $ cd docker
    $ docker build -t cloudnative/inventoryservice
    ```

3. Start application container using local image
    ```
    $ docker run -d -p 8080:8080 --name inventoryservice cloudnative/inventoryservice
    ```

4. Validate`http://<dockerhost>:8080/micro/inventory`

    
