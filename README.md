# Glasskube

> The Glasskube Kubernetes Operator enables automatic installation and updates for popular open source software solutions to help organisations reclaim their **digital sovereignty**.

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
minikube start --profile glasskube
```

### Tasks

```shell
./gradlew installCrd
./gradlew run
```

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

## Related projects

- Java client for Kubernetes [`fabric8io/kubernetes-client`](https://github.com/fabric8io/kubernetes-client)
- Java Operator SDK [`java-operator-sdk/java-operator-sdk`](https://github.com/java-operator-sdk/java-operator-sdk)

## Supported by

- Media Tech Lab [`media-tech-lab`](https://github.com/media-tech-lab)

<a href="https://www.media-lab.de/en/programs/media-tech-lab">
    <img src="https://raw.githubusercontent.com/media-tech-lab/.github/main/assets/mtl-powered-by.png" width="240" title="Media Tech Lab powered by logo">
</a>
