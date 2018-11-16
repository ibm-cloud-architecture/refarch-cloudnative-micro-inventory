###### refarch-cloudnative-micro-inventory

# Microservice Apps Integration with ElasticSearch and MySQL Database

*This project is part of the 'IBM Cloud Native Reference Architecture' suite, available at
https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes*

## Table of Contents

* [Introduction](#introduction)
* [Implementation](#implementation)
* [References](#references)

## Introduction

This project is built to demonstrate Microservice Apps Integration with ElasticSearch and MySQL Database. [Inventory](https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/tree/microprofile/inventory) uses MySQL database as its datasource.

- Uses MySQL as the [Inventory](https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/tree/microprofile/inventory) database.

<p align="center">
    <img src="images/inventory-catalog.png">
</p>

## Implementation

**Inventory service**

- [Microprofile](../../tree/microprofile/inventory) - leverages the Microprofile framework.
- [Spring](../../tree/spring) - leverages Spring Boot as the Java programming model of choice.


## References

- [Java MicroProfile](https://microprofile.io/)
- [Spring Boot](https://projects.spring.io/spring-boot/)
- [Kubernetes](https://kubernetes.io/)
- [Minikube](https://kubernetes.io/docs/getting-started-guides/minikube/)
- [Docker Edge](https://docs.docker.com/edge/)
- [IBM Cloud](https://www.ibm.com/cloud/)
- [IBM Cloud Private](https://www.ibm.com/cloud-computing/products/ibm-cloud-private/)
