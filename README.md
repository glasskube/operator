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

## Related projects

- Java client for Kubernetes [`fabric8io/kubernetes-client`](https://github.com/fabric8io/kubernetes-client)
- Java Operator SDK [`java-operator-sdk/java-operator-sdk`](https://github.com/java-operator-sdk/java-operator-sdk)

## Supported by

- Media Tech Lab [`media-tech-lab`](https://github.com/media-tech-lab)

<a href="https://www.media-lab.de/en/programs/media-tech-lab">
    <img src="https://raw.githubusercontent.com/media-tech-lab/.github/main/assets/mtl-powered-by.png" width="240" title="Media Tech Lab powered by logo">
</a>
