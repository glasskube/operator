[![GitHub Repo stars](https://img.shields.io/github/stars/glasskube/operator)](https://github.com/glasskube/operator)
[![Docker Pulls](https://img.shields.io/docker/pulls/glasskube/operator)](https://hub.docker.com/r/glasskube/operator)

<br>
<div align="center">
  <a href="https://glasskube.eu/">
    <img src="https://raw.githubusercontent.com/glasskube/.github/main/images/glasskube-logo.png" alt="Glasskube Logo" height="160">
  </a>

<h3 align="center">Kubernetes Operator</h3>

  <p align="center">
    Open Source Tools on autopilot
    <br><br>
    <a href="https://glasskube.eu/"><strong>Explore our website »</strong></a>
    <br>
    <br>
    <a href="https://github.com/glasskube" target="_blank">GitHub</a>
    .
    <a href="https://hub.docker.com/u/glasskube" target="_blank">Docker Hub</a>
    .
    <a href="https://www.linkedin.com/company/glasskube/" target="_blank">LinkedIn</a>
  </p>
</div>

<hr>

## Screencast

https://user-images.githubusercontent.com/3041752/217483828-2f0245bc-dbe8-4fc5-901f-e96131187c22.mp4

### Getting started

To deploy the operator and related RBAC resources, you can use the resources in `deploy/`.
The easiest method of installation is the `deploy.sh` script.
It will automatically install dependencies, CRDs and the Glasskube operator into your current `kubectl` context.
You can choose to let the operator manage custom resources in the entire cluster or just a single namespace.

#### Dependencies

The `deploy.sh` script will automatically install following dependencies:

- cert-manager [`cert-manager/cert-manager`](https://github.com/cert-manager/cert-manager)
- Prometheus, Grafana & Alert
  Manager [`prometheus-community/kube-prometheus-stack`](https://github.com/prometheus-community/helm-charts/blob/main/charts/kube-prometheus-stack/README.md)
- mariadb-operator [`mmontes11/mariadb-operator`](https://github.com/mmontes11/mariadb-operator)
- CloudNativePG [`cloudnative-pg/cloudnative-pg`](https://github.com/cloudnative-pg/cloudnative-pg)
- MinIO [`minio/minio`](https://github.com/minio/minio/tree/master/helm/minio)

> **Note**
> Installing CRDs still requires cluster-wide permissions.
> Currently it is not possible to exclude certain dependencies.


All configuration parameters can be printed with the `-h` command.

```txt
$ ./deploy/deploy.sh  -h

Usage: deploy.sh [options]

Options:
  -v  VERSION
       Check https://hub.docker.com/r/glasskube/operator/tags for available versions.
       If no version is provided, only dependencies are installed.
  -n  NAMESPACE
       Specifies the namespace where all operators will be installed. If NAMESPACE is
       missing or empty, a cluster-wide installation will be performed.
  -g  HOST_NAME
       Specifies the host name that should be used for the Grafana ingress. If no host
       name is provided, the Grafana ingress will not be enabled.
  -p  AMOUNT
       Specifies the size of the prometheus storage claim(e.g. "10Gi"). If no value is
       set, prometheus persistence will not be enabled.
  -i  INGRESS_CLASS
       Specifies the ingress class name to be used
  -c  CLUSTER_ISSUER
       Specifies the cluster issuer to be used
  -d  CLUSTER_DNS_NAME
       Specifies the cluster name to resolve dns queries
  -h
       Show this help.
````

Run the script for example like this:

```shell
deploy/deploy.sh -v $VERSION -g grafana.minikube -p 10Gi -i nginx
```

You can find the latest `$VERSION` on [GitHub](https://github.com/glasskube/operator/tags)
or [Docker Hub](https://hub.docker.com/r/glasskube/operator/tags).

## Custom Resources

The operator currently supports following custom resources:

### HttpEcho Kubernetes Operator

**HttpEcho.yaml**

```yaml
apiVersion: "glasskube.eu/v1alpha1"
kind: HttpEcho
metadata:
  name: echo
  namespace: default
spec:
  text: MTL Demo 🧊
```

[`http-echo`](https://github.com/hashicorp/http-echo) is a simple go web server that returns preconfigured text.
The Glasskube operator will create a deployment, service and ingress based on the applied custom resource.
The Webserver is reachable via [`http://echo.minikube`](http://echo.minikube).

### Matomo Kubernetes Operator

<img width="300px" src="https://matomo.org/wp-content/themes/website-child/assets/img/media/matomo.png" alt="Matomo logo"></img>

**Matomo.yaml**

```yaml
apiVersion: "glasskube.eu/v1alpha1"
kind: Matomo
metadata:
  name: matomo
  namespace: matomo
spec:
  host: matomo.minikube
```

[`Matomo`](https://github.com/matomo-org/matomo) is an Open-Source Web Analytics Tool written in `PHP` and stores data
in `MySQL` database. The Glasskube Operator will automatically perform Upgrades and manages the database.
Make sure you also have the [`mariadb-operator`](https://github.com/mmontes11/mariadb-operator) installed.
After applying the custom resource Matomo will be reachable via an ingress at the configured host.

### Odoo Kubernetes Operator

<img width="300px" src="https://raw.githubusercontent.com/odoo/documentation/16.0/static/img/odoo_logo.png" alt="Odoo logo"></img>

**Odoo.yaml**

```yaml
apiVersion: "glasskube.eu/v1alpha1"
kind: Odoo
metadata:
  name: odoo
  namespace: odoo
spec:
  host: odoo.minikube
```

[`Odoo`](https://github.com/odoo/odoo) is an Open-Source suite of web based business apps written in `Python` and stores
data in a `PostgreSQL` database. The Glasskube Operator will automatically perform Upgrades and manages the database.
Make sure you also have the [`cnpg-operator`](https://github.com/cloudnative-pg/cloudnative-pg) installed.
After applying the custom resource Odoo will be reachable via an ingress at the configured host.
Daily database backups are stored in an integrated S3 compatible MinIO bucket inside the glasskube-system namespace.

## Related projects

- Java client for Kubernetes [`fabric8io/kubernetes-client`](https://github.com/fabric8io/kubernetes-client)
- Java Operator SDK [`java-operator-sdk/java-operator-sdk`](https://github.com/java-operator-sdk/java-operator-sdk)

## How to Contribute

See [the contributing guide](CONTRIBUTING.md) for detailed instructions.

Also join our [`architecture discussion`](https://github.com/glasskube/operator/discussions/4) on GitHub.

## Supported by

- Media Tech Lab [`media-tech-lab`](https://github.com/media-tech-lab)

<a href="https://www.media-lab.de/en/programs/media-tech-lab">
    <img src="https://raw.githubusercontent.com/media-tech-lab/.github/main/assets/mtl-powered-by.png" width="240" title="Media Tech Lab powered by logo">
</a>
