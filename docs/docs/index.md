---
sidebar_position: 1
---

# Welcome

The Glasskube Operator is an open source Kubernetes operator that aims to simplify the deployment and maintenance of various popular open source tools. 
Each tool is represented by a new Kubernetes [custom resource definition](https://kubernetes.io/docs/concepts/extend-kubernetes/api-extension/custom-resources/) 
(CRD) and most user-facing configuration parameters are available via that CRD.
Our philosophy is to emphasize ease-of-use and strong defaults over rich configuration. 
Our configurations are designed to cover as many use-cases as possible with minimal user configuration.
Should you find that the Glasskube Operator does not fully cover your use-case, don't hesitate to 
[reach out](https://github.com/glasskube/operator/issues/new/choose).

## Features

- Easy installation with little to no configuration
- Automatic setup of databases, caches
- Scheduled database backups
- Supported open source tools:
  - [Gitea](crd-reference/gitea)
  - [GitLab](crd-reference/gitlab)
  - [GlitchTip](crd-reference/glitchtip)
  - [Keycloak](crd-reference/keycloak)
  - [Matomo](crd-reference/matomo)
  - [Metabase](crd-reference/metabase)
  - [Nextcloud](crd-reference/nextcloud)
  - [Odoo](crd-reference/odoo)
  - [Vault](crd-reference/vault)

## About Glasskube

The Glasskube Operator project was started by [Glasskube](https://glasskube.eu/), a pioneering European company that is transforming the way Open Source Software
is deployed in cloud native infrastructures. 
With a focus on automation and privacy, Glasskube offers managed Open Source Software on robust DACH infrastructure. 
This approach allows businesses to concentrate on their core competencies while ensuring that their software is constantly up-to-date 
and seamlessly scalable to meet evolving needs. 
Additionally, Glasskube places a strong emphasis on data protection and data security, ensuring that all information remains secure
within a privacy-focused environment. 

## About this documentation

This is the official documentation of the Glasskube Operator.
If you want to get started *right now*, head over to our [Getting started](getting-started/install) guide. 
If you are looking for specific information about one of our CRDs, take a look at our [CRD reference](crd-reference).  
