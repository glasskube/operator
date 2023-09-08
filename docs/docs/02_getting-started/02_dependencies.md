# Dependencies

Some tasks are delegated by the Glasskube Operator to other controllers or operators that must be installed in your cluster.
All dependencies can be installed alongside the operator when using the helm chart but are disabled by default (except for [minio](#minio)).
This document enumerates those dependencies and describes whether and for which use-cases they are required.

You could either install these dependencies implicitly by enabling them via the Glasskube Operator helm chart or manage them your self (recommended).
In that case you can disable the dependencies.
For more information and default values checkout [Glasskube on the ArtifactHUB](https://artifacthub.io/packages/helm/glasskube/glasskube-operator).

## Cert-manager

![Static Badge](https://img.shields.io/badge/always_required-gray)
[![License](https://img.shields.io/github/license/cert-manager/cert-manager)](https://github.com/cert-manager/cert-manager/blob/master/LICENSE)
[![Artifact Hub](https://img.shields.io/endpoint?url=https://artifacthub.io/badge/repository/cert-manager)](https://artifacthub.io/packages/helm/cert-manager/cert-manager)
[![GitHub](https://img.shields.io/github/stars/cert-manager/cert-manager)](https://github.com/cert-manager/cert-manager)

By default, the Glasskube Operator annotates *Ingress* resources it creates such that [cert-manager](https://cert-manager.io/) automatically generates TLS certificates.

If you do not wish to use this feature, Cert Manager does not have to be installed.
In that case, the *Ingress* annotations will simply be ignored.

## Prometheus Operator

![Static Badge](https://img.shields.io/badge/always_required-gray)
[![License](https://img.shields.io/github/license/prometheus-operator/kube-prometheus)](https://github.com/prometheus-operator/kube-prometheus/blob/master/LICENSE)
[![Artifact Hub](https://img.shields.io/endpoint?url=https://artifacthub.io/badge/repository/kube-prometheus-stack)](https://artifacthub.io/packages/helm/prometheus-community/kube-prometheus-stack)
[![GitHub](https://img.shields.io/github/stars/prometheus-operator/kube-prometheus)](https://github.com/prometheus-operator/kube-prometheus)

For some applications, the Glasskube Operator creates *ServiceMonitor* and/or *PodMonitor* resources, which are provided by the [Prometheus Operator](https://prometheus-operator.dev/).
The easiest way to install the Prometheus Operator is via the [`kube-prometheus-stack` helm chart](https://github.com/prometheus-community/helm-charts/tree/main/charts/kube-prometheus-stack).


## CloudnativePG

![Static Badge](https://img.shields.io/badge/always_required-gray)
[![License](https://img.shields.io/github/license/cloudnative-pg/cloudnative-pg)](https://github.com/cloudnative-pg/cloudnative-pg/blob/master/LICENSE)
[![Artifact Hub](https://img.shields.io/endpoint?url=https://artifacthub.io/badge/repository/cloudnative-pg)](https://artifacthub.io/packages/helm/cloudnative-pg/cloudnative-pg)
[![GitHub](https://img.shields.io/github/stars/cloudnative-pg/cloudnative-pg)](https://github.com/cloudnative-pg/cloudnative-pg)

The Glasskube Operator delegates creation and management of PostgreSQL instances to the [CloudnativePG Operator](https://cloudnative-pg.io/).

## MinIO

![Static Badge](https://img.shields.io/badge/always_required-gray)
[![License](https://img.shields.io/github/license/minio/minio)](https://github.com/minio/minio/blob/master/LICENSE)
[![Artifact Hub](https://img.shields.io/endpoint?url=https://artifacthub.io/badge/repository/minio-official)](https://artifacthub.io/packages/helm/minio-official/minio)
[![GitHub](https://img.shields.io/github/stars/minio/minio)](https://github.com/minio/minio)

The Glasskube Operator configures database instances to push WALs and backups to a local [MinIO](https://min.io/) cluster.

## MariaDB Operator

[![Static Badge](https://img.shields.io/badge/required_for-matomo-blue)](../03_crd-reference/matomo.md)
[![License](https://img.shields.io/github/license/mariadb-operator/mariadb-operator)](https://github.com/mariadb-operator/mariadb-operator/blob/master/LICENSE)
[![Artifact Hub](https://img.shields.io/endpoint?url=https://artifacthub.io/badge/repository/mariadb-operator)](https://artifacthub.io/packages/helm/mariadb-operator/mariadb-operator)
[![GitHub](https://img.shields.io/github/stars/mariadb-operator/mariadb-operator)](https://github.com/mariadb-operator/mariadb-operator)

The Glasskube Operator delegates creation and management of MariaDB instances for *Matomo* installations to the [MariaDB Operator](https://github.com/mariadb-operator/mariadb-operator).
