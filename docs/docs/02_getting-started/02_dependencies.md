# Dependencies

Some tasks are delegated by the Glasskube Operator to other controllers or operators that must be installed in your cluster.
All dependencies can be installed alongside the operator when using the helm chart but are disabled by default (except for [minio](#minio)).
This document enumerates those dependencies and describes whether and for which use-cases they are required.

You could either install these dependencies implicitly with the default values of the Glasskube Operator helm chart or manage them your self.
In that case you can disable the dependencies.
For more information and default values checkout [Glasskube on the ArtifactHUB](https://artifacthub.io/packages/helm/glasskube/glasskube-operator).

## Cert-manager

By default, the Glasskube Operator annotates *Ingress* resources it creates such that [cert-manager](https://cert-manager.io/) automatically generates TLS certificates.

If you do not wish to use this feature, Cert Manager does not have to be installed.
In that case, the *Ingress* annotations will simply be ignored.

## Prometheus Operator

For some applications, the Glasskube Operator creates *ServiceMonitor* and/or *PodMonitor* resources, which are provided by the [Prometheus Operator](https://prometheus-operator.dev/).
The easiest way to install the Prometheus Operator is via the [`kube-prometheus-stack` helm chart](https://github.com/prometheus-community/helm-charts/tree/main/charts/kube-prometheus-stack).

Installing this dependency is always required.

## MariaDB Operator

The Glasskube Operator delegates creation and management of MariaDB instances for *Matomo* installations to the [MariaDB Operator](https://github.com/mariadb-operator/mariadb-operator).

Installing this operator is not required if support for *Matomo* installations is not needed.

## CloudnativePG

The Glasskube Operator delegates creation and management of PostgreSQL instances to the [CloudnativePG Operator](https://cloudnative-pg.io/).

Installing this dependency is always required.

## MinIO

The Glasskube Operator configures database instances to push WALs and backups to a local [MinIO](https://min.io/) cluster.

Installing this dependency is always required.
