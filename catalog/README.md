# Cloudnative MicroProfile Microservice Integration with Elasticsearch

## Catalog Service - MicroProfile

This repository contains the **MicroProfile** implementation of the **Catalog Service** which is a part of 'IBM Cloud Native Reference Architecture' suite, available at https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes

<p align="center">
  <a href="https://microprofile.io/">
    <img src="https://github.com/ibm-cloud-architecture/refarch-cloudnative-wfd/blob/microprofile/static/imgs/microprofile_small.png" width="300" height="100">
  </a>
</p>

1. [Introduction](#introduction)
2. [How it works](#how-it-works)
3. [Implementation](#implementation)
    1. [Microprofile](#microprofile)
4. [Features and App details](#features)
5. [Building the app](#building-the-app)
6. [Setting up Elasticsearch](#setting-up-elasticsearch)
    1. [Elasticsearch Pre-requisites](#elasticsearch-pre-requisites)
    2. [Set Up Elasticsearch on Minikube](#set-up-elasticsearch-on-minikube)
    3. [Set Up Elasticsearch on IBM Cloud Private](#set-up-elasticsearch-on-ibm-cloud-private)
7. [Running the app and stopping it](#running-the-app-and-stopping-it)
    1. [Pre-requisites](#pre-requisites)
    2. [Locally in Minikube](#locally-in-minikube)
    3. [Remotely in ICP](#remotely-in-icp)
8. [References](#references)

### Introduction

This project demonstrates the implementation of Catalog Microservice. The catalog microservice uses Elastic Search and serves as cache to the Inventory service. 

- Based on [MicroProfile](https://microprofile.io/).
- Uses Elasticsearch.
- Devops - TBD
- Deployment options for Minikube environment and ICP.

### How it works

Catalog Microservice serves 'IBM Cloud Native Reference Architecture' suite, available at https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes, Microservice-based reference application. Though it is a part of a bigger application, Catalog service is itself an application in turn that serves as cache to the Inventory Service.

### API Endpoints

```
GET     /catalog/rest/items/              # Returns all items in inventory
GET     /catalog/rest/items/{id}          # Returns item by id 
```
### Implementation

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

You should also include a feature in [server.xml](https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/blob/microprofile/catalog/src/main/liberty/config/server.xml).

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
   
2. `cd refarch-cloudnative-micro-inventory/`

3. Checkout MicroProfile branch.

   `git checkout microprofile`
   
4. Go to Catalog ... `cd catalog`

5. Run this command. This command builds the project and installs it.

   `mvn install`
   
   If this runs successfully, you will be able to see the below messages.
   
```
[INFO] --- maven-failsafe-plugin:2.18.1:verify (verify-results) @ catalog ---
[INFO] Failsafe report directory: /Users/user@ibm.com/BlueCompute/refarch-cloudnative-micro-inventory/catalog/target/test-reports/it
[INFO] 
[INFO] --- maven-install-plugin:2.4:install (default-install) @ catalog ---
[INFO] Installing /Users/user@ibm.com/BlueCompute/refarch-cloudnative-micro-inventory/catalog/target/catalog-1.0-SNAPSHOT.war to /Users/user@ibm.com/.m2/repository/projects/catalog/1.0-SNAPSHOT/catalog-1.0-SNAPSHOT.war
[INFO] Installing /Users/user@ibm.com/BlueCompute/refarch-cloudnative-micro-inventory/catalog/pom.xml to /Users/user@ibm.com/.m2/repository/projects/catalog/1.0-SNAPSHOT/catalog-1.0-SNAPSHOT.pom
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 31.104 s
[INFO] Finished at: 2018-02-23T10:13:57-06:00
[INFO] Final Memory: 21M/250M
[INFO] ------------------------------------------------------------------------
```
### Setting up Elasticsearch

#### Elasticsearch Pre-requisites

1. Set Up Elasticsearch on Minikube

To set up Elasticsearch locally on your laptop on a Kubernetes-based environment such as Minikube (which is meant to be a small development environment), we first need to get few tools installed:

- [Kubectl](https://kubernetes.io/docs/user-guide/kubectl-overview/) (Kubernetes CLI) - Follow the instructions [here](https://kubernetes.io/docs/tasks/tools/install-kubectl/) to install it on your platform.
- [Helm](https://github.com/kubernetes/helm) (Kubernetes package manager) - Follow the instructions [here](https://github.com/kubernetes/helm/blob/master/docs/install.md) to install it on your platform.

Finally, we must create a Kubernetes Cluster. As already said before, we are going to use Minikube:

- [Minikube](https://kubernetes.io/docs/getting-started-guides/minikube/) - Create a single node virtual cluster on your workstation. Follow the instructions [here](https://kubernetes.io/docs/tasks/tools/install-minikube/) to get Minikube installed on your workstation.

We not only recommend to complete the three Minikube installation steps on the link above but also read the [Running Kubernetes Locally via Minikube](https://kubernetes.io/docs/getting-started-guides/minikube/) page for getting more familiar with Minikube. We can learn there interesting things such as reusing our Docker daemon, getting the Minikube's ip or opening the Minikube's dashboard for GUI interaction with out Kubernetes Cluster.

2. Set Up Elasticsearch on IBM Cloud Private

To set up Elasticsearch on IBM Cloud Private, you need to have the following.

[IBM Cloud Private Cluster](https://www.ibm.com/cloud/private)

Create a Kubernetes cluster in an on-premise datacenter. The community edition (IBM Cloud private-ce) is free of charge.
Follow the instructions [here](https://www.ibm.com/support/knowledgecenter/en/SSBS6K_2.1.0.2/installing/install_containers_CE.html) to install IBM Cloud private-ce.

[Helm](https://github.com/kubernetes/helm) (Kubernetes package manager)

Follow the instructions [here](https://github.com/kubernetes/helm/blob/master/docs/install.md) to install it on your platform.
If using IBM Cloud Private version 2.1.0.2 or newer, we recommend you follow these [instructions](https://www.ibm.com/support/knowledgecenter/SSBS6K_2.1.0.2/app_center/create_helm_cli.html) to install helm.

#### Set Up Elasticsearch on Minikube 

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

`helm install --name=es chart/ibmcase-elasticsearch`

6. Make sure your deployment is ready. To verify run this command and you should see the availability.

`kubectl get deployments`

Yow will see message like below.

```
==> v1beta1/Deployment
NAME                              DESIRED  CURRENT  UP-TO-DATE  AVAILABLE  AGE
es-default-cluster-elasticsearch  1        1        1           0          0s
```
Please wait till your deployment is ready. To verify run the below command and you should see the availability.

`kubectl get deployments`

You will see something like below.

```
==> v1beta1/Deployment
NAME                               DESIRED   CURRENT   UP-TO-DATE   AVAILABLE   AGE
es-default-cluster-elasticsearch   1         1         1            1           3m
```

#### Set Up Elasticsearch on IBM Cloud Private

1. Your [IBM Cloud Private Cluster](https://www.ibm.com/cloud/private) should be up and running.

2. Log in to the IBM Cloud Private. 

<p align="center">
    <img src="https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes/blob/microprofile/static/imgs/icp_dashboard.png">
</p>

3. Go to `admin > Configure Client`.

<p align="center">
    <img src="https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes/blob/microprofile/static/imgs/client_config.png">
</p>

4. Grab the kubectl configuration commands.

<p align="center">
    <img src="https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes/blob/microprofile/static/imgs/kube_cmds.png">
</p>

5. Run those commands in your terminal.

6. If successful, you should see something like below.
```
Switched to context "xxx-cluster.icp-context".
```
7. Run the below command.

`helm init --client-only`

You will see the below

```
$HELM_HOME has been configured at /Users/user@ibm.com/.helm.
Not installing Tiller due to 'client-only' flag having been set
Happy Helming!
```

8. Verify the helm version

`helm version --tls`

You will see something like below.

```
Client: &version.Version{SemVer:"v2.7.2+icp", GitCommit:"d41a5c2da480efc555ddca57d3972bcad3351801", GitTreeState:"dirty"}
Server: &version.Version{SemVer:"v2.7.2+icp", GitCommit:"d41a5c2da480efc555ddca57d3972bcad3351801", GitTreeState:"dirty"}
```

9. Run the helm chart as below.

`helm install --name=es chart/ibmcase-elasticsearch`

10. Make sure your deployment is ready. To verify run this command and you should see the availability.

`kubectl get deployments`

Yow will see message like below.

```
==> v1beta1/Deployment
NAME                              DESIRED  CURRENT  UP-TO-DATE  AVAILABLE  AGE
es-default-cluster-elasticsearch  1        1        1           0          0s
```
Please wait till your deployment is ready. To verify run the below command and you should see the availability.

`kubectl get deployments`

You will see something like below.

```
==> v1beta1/Deployment
NAME                               DESIRED   CURRENT   UP-TO-DATE   AVAILABLE   AGE
es-default-cluster-elasticsearch   1         1         1            1           3m
``` 

**NOTE**: If you are using a version of ICP older than 2.1.0.2, you don't need to add the --tls at the end of the helm command.

### Running the app and stopping it

#### Pre-requisites

As Catalog service serves as cache to the Inventory service, make sure the inventory service is up and running before running the Catalog.

To run the Catalog microservice, please complete the [Building the app](#building-the-app) section before proceeding to the following steps.

1. Locally in Minikube

To run the Catalog application locally on your laptop on a Kubernetes-based environment such as Minikube (which is meant to be a small development environment) we first need to get few tools installed:

- [Kubectl](https://kubernetes.io/docs/user-guide/kubectl-overview/) (Kubernetes CLI) - Follow the instructions [here](https://kubernetes.io/docs/tasks/tools/install-kubectl/) to install it on your platform.
- [Helm](https://github.com/kubernetes/helm) (Kubernetes package manager) - Follow the instructions [here](https://github.com/kubernetes/helm/blob/master/docs/install.md) to install it on your platform.

Finally, we must create a Kubernetes Cluster. As already said before, we are going to use Minikube:

- [Minikube](https://kubernetes.io/docs/getting-started-guides/minikube/) - Create a single node virtual cluster on your workstation. Follow the instructions [here](https://kubernetes.io/docs/tasks/tools/install-minikube/) to get Minikube installed on your workstation.

We not only recommend to complete the three Minikube installation steps on the link above but also read the [Running Kubernetes Locally via Minikube](https://kubernetes.io/docs/getting-started-guides/minikube/) page for getting more familiar with Minikube. We can learn there interesting things such as reusing our Docker daemon, getting the Minikube's ip or opening the Minikube's dashboard for GUI interaction with out Kubernetes Cluster.

2. Remotely in ICP

[IBM Cloud Private Cluster](https://www.ibm.com/cloud/private)

Create a Kubernetes cluster in an on-premise datacenter. The community edition (IBM Cloud private-ce) is free of charge.
Follow the instructions [here](https://www.ibm.com/support/knowledgecenter/en/SSBS6K_2.1.0.2/installing/install_containers_CE.html) to install IBM Cloud private-ce.

[Helm](https://github.com/kubernetes/helm) (Kubernetes package manager)

Follow the instructions [here](https://github.com/kubernetes/helm/blob/master/docs/install.md) to install it on your platform.
If using IBM Cloud Private version 2.1.0.2 or newer, we recommend you follow these [instructions](https://www.ibm.com/support/knowledgecenter/SSBS6K_2.1.0.2/app_center/create_helm_cli.html) to install helm.

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

`docker build -t catalog:v1.0.0 .`

If it is a success, you will see the below output.

```
Successfully built aee7ed946e93
Successfully tagged catalog:v1.0.0
```

2. Run the helm chart as below.

Before running the helm chart in minikube, access [values.yaml](https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/blob/microprofile/catalog/chart/catalog/values.yaml) and replace the repository with the below.

`repository: catalog`

Then run the helm chart 

`helm install --name=catalog chart/catalog`

You will see message like below.

```
==> v1beta1/Deployment
NAME                DESIRED  CURRENT  UP-TO-DATE  AVAILABLE  AGE
catalog-deployment  1        1        1           0          1s
```
Please wait till your deployment is ready. To verify run the below command and you should see the availability.

`kubectl get deployments`

You will see something like below.

```
==> v1beta1/Deployment
NAME                               DESIRED   CURRENT   UP-TO-DATE   AVAILABLE   AGE
catalog-deployment                 1         1         1            1           13m
```

3. You can access the application at `http://<MinikubeIP>:<PORT>/<WAR_CONTEXT>/<APPLICATION_PATH>/<ENDPOINT>`. To get the access url.

- To get the IP, Run this command.

`minikube ip`

You will see something like below.

```
192.168.99.100
```

- To get the port, run this command.

`kubectl get service catalog-service`

You will see something like below.

```
NAME              CLUSTER-IP    EXTERNAL-IP   PORT(S)                         AGE
catalog-service   10.99.41.25   <nodes>       9080:30939/TCP,9443:30146/TCP   17m
```

In the above case, the access url will be `http://192.168.99.100:30939/catalog/rest/items`.

4. Validate the catalog in the following way.

`curl -X GET http://192.168.99.100:30939/catalog/rest/items`

You should now see a list of inventory items in your terminal.

```
[{"id":13405,"name":"Electric Card Collator","description":"The IBM 77 electric punched card collator performed many card filing and pulling operations. As a filing machine, the Type 77 fed and compared simultaneously two groups of punched cards: records already in file and records to be filed. These two groups were merged in correct numerical or alphabetical sequence. When operated for the purpose of pulling cards, the Type 77 made it possible for one group of cards to pull corresponding cards from another group. Introduced in 1937, the IBM 77 collator rented for 0 a month. It was capable of handling 240 cards a minute, and was 40.5 inches long and 51 inches high. IBM withdrew the Type 77 from marketing on November 27, 1957.","price":3899,"imgAlt":"IBM 77 Electric Punched Card Collator","img":"electric-card-punch.jpg","stock":1000}, _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _  _ _ _ ]
```
### Remotely in ICP

[IBM Cloud Private](https://www.ibm.com/cloud/private)

IBM Private Cloud has all the advantages of public cloud but is dedicated to single organization. You can have your own security requirements and customize the environment as well. Basically it has tight security and gives you more control along with scalability and easy to deploy options. You can run it externally or behind the firewall of your organization.

Basically this is an on-premise platform.

Includes docker container manager
Kubernetes based container orchestrator
Graphical user interface
You can find the detailed installation instructions for IBM Cloud Private [here](https://www.ibm.com/support/knowledgecenter/en/SSBS6K_2.1.0.2/installing/install_containers_CE.html)

#### Pushing the image to Private Registry

1. Now run the docker build.

`docker build -t catalog:v1.0.0 .`

If it is a success, you will see the below output.

```
Successfully built aee7ed946e93
Successfully tagged catalog:v1.0.0
```

2. Tag the image to your private registry.

`docker tag catalog:v1.0.0 <Your ICP registry>/catalog:v1.0.0`

3. Push the image to your private registry.

`docker push <Your ICP registry>/catalog:v1.0.0`

You should see something like below.

```
v1.0.0: digest: sha256:7f3deb2c43854df725efde5b0a3e6977cc7b6e8e26865b484d8cb20c2e4a6dd0 size: 3873
```

#### Running the application on ICP

1. Your [IBM Cloud Private Cluster](https://www.ibm.com/cloud/private) should be up and running.

2. Log in to the IBM Cloud Private. 

<p align="center">
    <img src="https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes/blob/microprofile/static/imgs/icp_dashboard.png">
</p>

3. Go to `admin > Configure Client`.

<p align="center">
    <img src="https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes/blob/microprofile/static/imgs/client_config.png">
</p>

4. Grab the kubectl configuration commands.

<p align="center">
    <img src="https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes/blob/microprofile/static/imgs/kube_cmds.png">
</p>

5. Run those commands in your terminal.

6. If successful, you should see something like below.

```
Switched to context "xxx-cluster.icp-context".
```
7. Run the below command.

`helm init --client-only`

You will see the below

```
$HELM_HOME has been configured at /Users/user@ibm.com/.helm.
Not installing Tiller due to 'client-only' flag having been set
Happy Helming!
```

8. Verify the helm version

`helm version --tls`

You will see something like below.

```
Client: &version.Version{SemVer:"v2.7.2+icp", GitCommit:"d41a5c2da480efc555ddca57d3972bcad3351801", GitTreeState:"dirty"}
Server: &version.Version{SemVer:"v2.7.2+icp", GitCommit:"d41a5c2da480efc555ddca57d3972bcad3351801", GitTreeState:"dirty"}
```

9. Before running the helm chart in minikube, access [values.yaml](https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/blob/microprofile/catalog/chart/catalog/values.yaml) and replace the repository with the below.

`repository: <Your IBM Cloud Private Docker registry>`

Then run the helm chart.

`helm install --name=catalog chart/catalog --tls`

You will see message like below.

```
==> v1beta1/Deployment
NAME                DESIRED  CURRENT  UP-TO-DATE  AVAILABLE  AGE
catalog-deployment  1        1        1           0          1s
```
Please wait till your deployment is ready. To verify run the below command and you should see the availability.

`kubectl get deployments`

You will see something like below.

```
==> v1beta1/Deployment
NAME                               DESIRED   CURRENT   UP-TO-DATE   AVAILABLE   AGE
catalog-deployment                 1         1         1            1           2m       
```

10. You can access the application at `http://<YourClusterIP>:<PORT>/<WAR_CONTEXT>/<APPLICATION_PATH>/<ENDPOINT>`. To get the access url.

- To get the IP, Run this command.

`kubectl cluster-info`

You will see something like below.

```
Kubernetes master is running at https://172.16.40.4:8001
catalog-ui is running at https://172.16.40.4:8001/api/v1/namespaces/kube-system/services/catalog-ui/proxy
Heapster is running at https://172.16.40.4:8001/api/v1/namespaces/kube-system/services/heapster/proxy
icp-management-ingress is running at https://172.16.40.4:8001/api/v1/namespaces/kube-system/services/icp-management-ingress/proxy
image-manager is running at https://172.16.40.4:8001/api/v1/namespaces/kube-system/services/image-manager/proxy
KubeDNS is running at https://172.16.40.4:8001/api/v1/namespaces/kube-system/services/kube-dns/proxy
platform-ui is running at https://172.16.40.4:8001/api/v1/namespaces/kube-system/services/platform-ui/proxy
```

Grab the Kubernetes master ip and in this case, `<YourClusterIP>` will be `172.16.40.4`.

- To get the port, run this command.

`kubectl get service catalog-service`

You will see something like below.

```
NAME              CLUSTER-IP     EXTERNAL-IP   PORT(S)                         AGE
catalog-service   10.10.10.170   <nodes>       9080:32681/TCP,9443:30834/TCP   4m
```

In the above case, the access url will be `http://172.16.40.182:32681/catalog/rest/items`.

<p align="center">
    <img src="https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes/blob/microprofile/static/imgs/catalog_icp.png">
</p> 

or

You can access the service from dashboard.

- **Menu > Network Access > Services**
- Find `catalog-service` in the list and click on it.

<p align="center">
    <img src="https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes/blob/microprofile/static/imgs/catalog_service.png">
</p>

You can access the application by clicking on http link.

<p align="center">
    <img src="https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes/blob/microprofile/static/imgs/catalog_icp.png">
</p>

**NOTE**: If you are using a version of ICP older than 2.1.0.2, you don't need to add the --tls at the end of the helm command.

### References

1. [Developer Tools CLI](https://console.bluemix.net/docs/cloudnative/dev_cli.html#developercli)
2. [IBM Cloud Private](https://www.ibm.com/support/knowledgecenter/en/SSBS6K_2.1.0/kc_welcome_containers.html)
3. [IBM Cloud Private Installation](https://github.com/ibm-cloud-architecture/refarch-privatecloud)
4. [IBM Cloud Private version 2.1.0.2 Helm instructions](https://www.ibm.com/support/knowledgecenter/SSBS6K_2.1.0.2/app_center/create_helm_cli.html)
5. [Microprofile](https://microprofile.io/)


