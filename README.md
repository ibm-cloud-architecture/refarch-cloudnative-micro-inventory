###### refarch-cloudnative-micro-inventory

## Microprofile based Microservice Apps Integration with ElasticSearch and MySQL Database Server

This repository contains the **MicroProfile** implementation of the **Inventory Service** and **Catalog Service** which are a part of the 'IBM Cloud Native Reference Architecture' suite, available at https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes

<p align="center">
  <a href="https://microprofile.io/">
    <img src="https://github.com/ibm-cloud-architecture/refarch-cloudnative-wfd/blob/microprofile/static/imgs/microprofile_small.png" width="300" height="100">
  </a>
</p>

### Introduction

This project is built to demonstrate how to build two Microservices applications using Microprofile. The first application [Inventory](https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/tree/microprofile/inventory) uses MySQL database as its datasource. The second application [Catalog](https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/tree/microprofile/catalog) serves as a cache to [Inventory](https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/tree/microprofile/inventory) by leveraging Elasticsearch as its datasource.

- Based on [MicroProfile](https://microprofile.io/).
- Uses MySQL as the [Inventory](https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/tree/microprofile/inventory) database.
- Elasticsearch is used as the [Catalog](https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/tree/microprofile/catalog) microservice's data source.
- Devops - TBD
- Deployment options for local, Docker Container-based runtimes, Minikube environment and ICP/BMX.

### How it works

Inventory and Catalog Microservices serves 'IBM Cloud Native Reference Architecture' suite, available at
https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes. Though it is a part of a bigger application, Inventory and Catalog services are themselves individual applications.

<p align="center">
    <img src="https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes/blob/microprofile/static/imgs/inventory-catalog.png">
</p>

### API Endpoints

```
GET     /catalog/rest/items/              # Returns all items in inventory
GET     /catalog/rest/items/{id}          # Returns item by id 
```
You can use cURL or Chrome POSTMAN to send get/post/put/delete requests to the application.

### Implementation

For the implementation of the inventory service, please follow this [link](https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/tree/microprofile/inventory).

For the implementation of the catalog service, please follow this [link](https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/tree/microprofile/catalog).

Make sure you first start with the inventory service. The catalog must be implemented after the inventory because it is dependent on it.
