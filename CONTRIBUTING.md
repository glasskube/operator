# Contributing guide for the Glasskube Operator

Welcome, and thank you for deciding to invest some of your time in contributing to the Glasskube project!
The goal of this document is to define some guidelines to streamline our contribution workflow.

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

#### Liniting the project

```shell
./gradlew --continue ktlintCheck
```

#### Running the Operator

The operator is started locally and connects to your current `kubectl` context. As Glasskube might get installed in
different namespaces, we need to fetch the current namespace form the Kubernetes API. For local development we can
manually overwrite this with by setting the environment variable `NAMESPACE=glasskube-system`.

```shell
./gradlew run
```

#### Pushing an image to local minikube

When using Minikube for development, you can push a snapshot of the operator to the cluster:

```shell
./gradlew loadImage
```

## Issues

Take a look at our issues board if you want to learn about current tasks.

## Making changes

1. If you want to submit a change, start by searching for a related issue or creating a new one.
   Please, let us know what you are working on so we are able to give feedback as early as possible.
2. Fork this repository and check out your fork.
3. Create a working branch.
4. Start working on your changes.
5. Commit your changes (see below).
6. Create a pull request, once you feel ready.

## Committing a Change

We require all commits in this repository to adhere to the following commit message format.

```
<type>: <description> (#<issue number>)

[optional body]
```

The following `<type>`s are available:

* `fix` (bug fix)
* `feat` (includes new feature)
* `docs` (update to our documentation)
* `build` (update to the build config)
* `perf` (performance improvement)
* `style` (code style change without any other changes)
* `refactor` (code refactoring)
* `chore` (misc. routine tasks; e.g. dependency updates)

This format is based on [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/).
Please refer to the Conventional Commits specification for more details.
