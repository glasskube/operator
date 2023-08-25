# Install with helm

## Prerequisites

In order to use the Glasskube Operator you need a Kubernetes cluster. Managed Clusters are available at most cloud
providers, but if you just want to play around and get started quickly, you can use
[`minikube`](https://minikube.sigs.k8s.io/) to start a development cluster locally.

You will also need the following command line interface (CLI) tools on your local machine:

- `kubectl`
- `helm`

For installing the operator with default values we recommend a fresh Kubernetes Cluster. 

## Operator Installation

First, add our helm repository and refresh your repository list. 

```
helm repo add glasskube https://charts.glasskube.eu/
helm repo update
```

Next, install the Glasskube Operator with helm.

```
helm install my-glasskube-operator glasskube/glasskube-operator
```

The installation can be customized via helm values. To view all available values, run

```
helm show values glasskube/glasskube-operator
```

