[![GitHub Repo stars](https://img.shields.io/github/stars/glasskube/operator)](https://github.com/glasskube/operator)
[![Docker Pulls](https://img.shields.io/docker/pulls/glasskube/operator)](https://hub.docker.com/r/glasskube/operator)
[![license](https://img.shields.io/badge/license-LGPL_3.0-blue)](https://opensource.org/license/lgpl-3-0/)
[![Docs](https://img.shields.io/badge/docs-glasskube.eu%2Fdocs-blue)](https://glasskube.eu/docs/)
[![Artifact Hub](https://img.shields.io/endpoint?url=https://artifacthub.io/badge/repository/glasskube)](https://artifacthub.io/packages/helm/glasskube/glasskube-operator)


<br>
<div align="center">
  <a href="https://glasskube.eu/">
    <img src="https://raw.githubusercontent.com/glasskube/.github/main/images/glasskube-logo.png" alt="Glasskube Logo" height="160">
  </a>

<h3 align="center">Kubernetes Operator</h3>

  <p align="center">
    Open Source Tools on autopilot
    <br><br>
    <a href="https://glasskube.eu/docs/getting-started/install"><strong>Getting started »</strong></a>
    <br> <br>
    <a href="https://glasskube.eu/"><strong>Explore our website »</strong></a>
    <br>
    <br>
    <a href="https://github.com/glasskube" target="_blank">GitHub</a>
    .
    <a href="https://hub.docker.com/u/glasskube" target="_blank">Docker Hub</a>
    .
    <a href="https://artifacthub.io/packages/helm/glasskube/glasskube-operator" target="_blank">Artifact Hub</a>
    .
    <a href="https://www.linkedin.com/company/glasskube/" target="_blank">LinkedIn</a>
  </p>
</div>

<hr>

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

## Features

- Easy installation with little to no configuration
- Automatic setup of databases, caches
- Scheduled database backups
- Supported open source tools:
  - [Gitea](https://glasskube.eu/docs/crd-reference/gitea)
  - [GitLab](https://glasskube.eu/docs/crd-reference/gitlab)
  - [GlitchTip](https://glasskube.eu/docs/crd-reference/glitchtip)
  - [Keycloak](https://glasskube.eu/docs/crd-reference/keycloak)
  - [Matomo](https://glasskube.eu/docs/crd-reference/matomo)
  - [Metabase](https://glasskube.eu/docs/crd-reference/metabase)
  - [Nextcloud](https://glasskube.eu/docs/crd-reference/nextcloud)
  - [Odoo](https://glasskube.eu/docs/crd-reference/odoo)
  - [Vault](https://glasskube.eu/docs/crd-reference/vault)
