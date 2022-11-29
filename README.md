# Glasskube

> The Glasskube Kubernetes Operator enables automatic installation and updates for popular open source software
> solutions to help organisations reclaim their **digital sovereignty**.

## How to Contribute

See [the contributing guide](CONTRIBUTING.md) for detailed instructions.

Also join our [`architecture discussion`](https://github.com/glasskube/operator/discussions/4) on GitHub.

## Getting started

### Requirements

- Docker [`moby/moby`](https://github.com/moby/moby)
- Minikube [`kubernetes/minikube`](https://github.com/kubernetes/minikube)
- Gradle [`gradle/gradle`](https://github.com/gradle/gradle)
- Kotlin [`jetbrains/kotlin`](https://github.com/jetbrains/kotlin)

### Local Kubernetes setup

Minikube is the recommended way on starting a cluster for local development.

```shell
minikube profile glasskube # (optional)
minikube start
```

### Tasks

#### Applying Custom Resource Definitions

This task uses your current `kubectl` context.

```shell
./gradlew installCrd
```

#### Running the Operator

The operator is started locally and connects to your current `kubectl` context.

```shell
./gradlew run
```

#### Deploying the operator

When using Minikube for development, you can push a snapshot of the operator to the cluster:

```shell
./gradlew loadImage
```

To deploy the operator and related RBAC resources, you can use the resources in `deploy/`.

```shell
kubectl apply -k deploy/
```

**Note:** This will deploy the operator, but not any of the Custom Resource Definitions the operator acts upon.

## Custom Resources

The operator currently supports following custom resources:

### HttpEcho

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

## Related projects

- Java client for Kubernetes [`fabric8io/kubernetes-client`](https://github.com/fabric8io/kubernetes-client)
- Java Operator SDK [`java-operator-sdk/java-operator-sdk`](https://github.com/java-operator-sdk/java-operator-sdk)

## Supported by

- Media Tech Lab [`media-tech-lab`](https://github.com/media-tech-lab)

<a href="https://www.media-lab.de/en/programs/media-tech-lab">
    <img src="https://raw.githubusercontent.com/media-tech-lab/.github/main/assets/mtl-powered-by.png" width="240" title="Media Tech Lab powered by logo">
</a>
