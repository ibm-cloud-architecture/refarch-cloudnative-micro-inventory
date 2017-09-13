###### refarch-cloudnative-micro-inventory

## Spring Boot Netflix OSS Microservice Apps Integration with ElasticSearch and MySQL Database Server

*This project is part of the 'IBM Cloud Native Reference Architecture' suite, available at
https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes*

## Table of Contents
- **[Introduction](#introduction)**
    - [APIs](#apis)
- **[Pre-requisites](#pre-requisites)**
    - [CLIs](#clis)
    - [Message Hub](#message-hub)
    - [MySQL](#mysql)
    - [Elasticsearch](#elasticsearch)
- **[Run Inventory and Catalog Locally](#run-inventory-and-catalog-locally)**
    - [Run Inventory Service application on localhost](#run-inventory-service-application-on-localhost)
    - [Run Catalog Service application on localhost](#run-catalog-service-application-on-localhost)
- **[Deploy Inventory and Catalog to Kubernetes Cluster](#deploy-inventory-and-catalog-to-kubernetes-cluster)**
    - [Deploy Inventory Service application to Kubernetes Cluster](#deploy-inventory-service-application-to-kubernetes-cluster)
    - [Deploy Catalog Service application to Kubernetes Cluster](#deploy-catalog-service-application-to-kubernetes-cluster)

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
Clone git repository before getting started.

    ```
    # git clone http://github.com/refarch-cloudnative-micro-inventory.git
    # cd refarch-cloudnative-micro-inventory
    ```

### CLIs
To install the CLIs for Bluemix, Kubernetes, Helm, JQ, and YAML,  Run the following script to install the CLIs:

    `$ ./install_cli.sh`

### Message Hub
1. [Provision](https://console.ng.bluemix.net/catalog/services/message-hub) an instance of Message Hub into your Bluemix space.
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
5. Keep those credential handy for when deploying the Inventory and Catalog services in the following sections.

### MySQL
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
5. Keep those credential handy for when deploying the Inventory and Catalog services in the following sections.
6. **Create `items` table and load sample data. You should see message _Data loaded to inventorydb.items._**

    ```
    # cd mysql/scripts
    # bash load-data.sh {USER} {PASSWORD} {HOST} {PORT}
    ```
 
    - Replace `{USER}` with MySQL user.
    - Replace `{PASSWORD}` with MySQL password.
    - Replace `{HOST}` with MySQL host.
    - Replace `{PORT}` with MySQL port.

MySQL database is now setup in Compose.

### Elasticsearch
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
5. Keep those credential handy for when deploying the Inventory and Catalog services in the following sections.

Elasticsearch database is now setup in Compose.

## Run Inventory and Catalog Locally
In this section you will learn how to build and run the Inventory and Catalog apps locally.

### Run Inventory Service application on localhost
In this section you will run the Spring Boot application to run on your localhost.

1. **Change to `inventory` directory**.

    ```
    # cd inventory
    ```

2. Open `src/main/resources/application.yml` file.

3. **If not already done, [Provision `Message Hub` service instance](#message-hub)**.
    - After provisioning, go to instance `Service Credentials` tab on Bluemix, then press `View credentials`.
    - Open `src/main/resources/application.yml`, go to `message_hub` section, then copy and paste required `message_hub` fields using credentials from above.

4. **If not already done, [Provision a `MySQL` database on Compose](#mysql)**. Then replace the values for the fields in the `datasource` section with those obtained in the [MySQL Section](#mysql):
    - In the `url` field, type `jdbc:mysql://host:port/inventorydb`, and enter the values for `host` and `port`.
    - Enter values for `user`, `password`, and `port`.

5. **If not already done, [Provision an `Elasticsearch` database on Compose](#elasticsearch)**. Then replace the values for the fields in the `elasticsearch` section with those obtained in the [Elasticsearch Section](#elasticsearch):
    - In the `url` field, type `https://host:port`, and enter the values for `host` and `port`.
    - Enter values for `user` and `password`

6. **Build the application**.

    ```
    # ./gradlew build -x test
    ```

7. **Run the application on localhost**.

    ```
    # java -jar build/libs/micro-inventory-0.0.1.jar
    ```

8. **Validate. You should get a list of all inventory items**.

    ```
    # curl http://localhost:8080/micro/inventory
    ```

### Run Catalog Service application on localhost
In this section you will run the Catalog Spring Boot application to run on your localhost.

1. **Change to `catalog` directory**.

    ```
    # cd catalog
    ```

2. **If not already done, [Provision an `Elasticsearch` database on Compose](#elasticsearch)**. Then replace the values for the fields in the `elasticsearch` section with those obtained in the [Elasticsearch Section](#elasticsearch):
    - In the `url` field, type `https://host:port`, and enter the values for `host` and `port`.
    - Enter values for `user` and `password`

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

## Deploy Inventory and Catalog Applications to Kubernetes Cluster
In this section you will deploy the Inventory and Catalog applications to run on your Bluemix Kubernetes Cluster. 
We packaged the entire application stack as a Kubernetes [Chart](https://github.com/kubernetes/charts). To deploy the Inventory and Catalog Charts, please follow the instructions in the following sections.

#### Deploy Inventory to Paid Cluster

##### Easy way
We created a couple of handy scripts to deploy the Inventory chart for you. Please run the following command.

  ```
  # This script will install Inventory Chart
  # If you don't provide a cluster name, then it will try to get an
  # existing cluster for you, though it is not guaranteed to be the one
  # that you intended to deploy to. So use CAREFULLY.

  $ ./install_inventory.sh <cluster-name> <Optional:bluemix-space-name> <Optional:bluemix-api-key>
  ```

Once the actual install of Inventory takes place, it takes about 3-5 minutes to finish and show debug output. So it might look like it's stuck, but it's not. Once you start to see output, look for the `Bluecompute was successfully installed!` text in green, which indicates that the deploy was successful and cleanup of jobs and installation pods will now take place.

That's it! **Inventory is now installed** in your Kubernetes Cluster. To see the Kubernetes dashboard, run the following command:

  `$ kubectl proxy`

Then open a browser and paste the following URL to see the **Services** created by Inventory Chart:

  http://127.0.0.1:8001/api/v1/proxy/namespaces/kube-system/services/kubernetes-dashboard/#/service?namespace=default

If you like to see **installation progress** as it occurs, open a browser window and paste the following URL to see the Installation Jobs. About 17 jobs will be created in sequence:

  http://127.0.0.1:8001/api/v1/proxy/namespaces/kube-system/services/kubernetes-dashboard/#/job?namespace=default

Be mindful that the jobs will dissapear once the `Cleaning up` message is displayed by *install_inventory.sh*.

**Notes:**

The *install_inventory.sh* script will do the following:
1. **Ask you login to Bluemix.**
2. **Initialize Container Plugin (bx cs init).**
3. **Get cluster configuration and set your terminal context to the cluster.**
4. **Initialize Helm.**
5. **Install *bluecompute-inventory* Chart.**
    * `$ helm install bluecompute-inventory`
    * It will create all the necessary configurations before deploying any pods.
6. **Cleanup Jobs and Pods used to deploy dependencies.**


##### Manual Way
If you like to run the steps manually, please follow the steps below:

1. ***Get paid cluster name*** by running the command below & then copy it to your clipboard:

    `$ bx cs clusters`

2. ***Set your terminal context to your cluster***:

    `$ bx cs cluster-config <cluster-name>`

    In the output to the command above, the path to your configuration file is displayed as a command to set an environment variable, for example:
    ```
    ...
    export KUBECONFIG=/Users/ibm/.bluemix/plugins/cs-cli/clusters/pr_firm_cluster/kube-config-dal10-pr_firm_cluster.yml
    ```

3. ***Set the `KUBECONFIG` Kubernetes configuration file*** using the ouput obtained with the above command:

    `$ export KUBECONFIG=/Users/ibm/.bluemix/plugins/cs-cli/clusters/pr_firm_cluster/kube-config-dal10-pr_firm_cluster.yml`

4. ***Initialize Helm***, which will be used to install Bluecompute Chart:

    `$ helm init --upgrade`

    Helm will install `Tiller` agent (Helm's server side) into your cluster, which enables you to install Charts on your cluster. The `--upgrade` flag is to make sure that both Helm client and Tiller are using the same Helm version.

5. ***Make sure that Tiller agent is fully Running*** before installing chart.

    `$ kubectl --namespace=kube-system get pods | grep tiller`

    To know whether Tiller is Running, you should see an output similar to this:

    `tiller-deploy-3210876050-l61b3              1/1       Running   0          1d`

6. If you don't have a ***BLUEMIX API Key***, create one as follows:

    `$ bx iam api-key-create bluekey`

7. ***Install bluecompute-inventory Chart***. The process usually takes between 3-5 minutes to finish and start showing debugging output:

    ```
    $ time helm install \
      --set configMap.bluemixOrg=${ORG} \
      --set configMap.bluemixSpace=${SPACE} \
      --set configMap.kubeClusterName=${CLUSTER_NAME} \
      --set secret.apiKey=${API_KEY} \
      . --debug
    ```

    * Replace ${ORG} with your Bluemix Organization name.
    * Replace ${SPACE} with your Bluemix Space.
    * Replace ${CLUSTER_NAME} with your Kubernetes Cluster name from Step 1.
    * Replace ${API_KEY} with the Bluemix API Key from Step 6.

That's it! **Inventory is now installed** in your Kubernetes Cluster. To see the Kubernetes dashboard, run the following command:

  `$ kubectl proxy`

Then open a browser and paste the following URL to see the **Services** created by Inventory Chart:

  http://127.0.0.1:8001/api/v1/proxy/namespaces/kube-system/services/kubernetes-dashboard/#/service?namespace=default
