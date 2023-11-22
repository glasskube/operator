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

## ⭐️ Why Glasskube?

Turn on autopilot and deploy and manage Open Source Tools fully automated on Kubernetes. Our Open Source Glasskube Operator is the simplest and fastest way to manage all your favorite Open Source Tools and the related infrastructure components like databases, caches, and keep them up to date without manual hassle.

## ✨ Features

- 👌 Easy installation with little to no configuration
- ✅ Automatic setup of databases and caches
- 🔄 Automate and schedule version updates and patches
- 🗓️ Scheduled database backups
- ⚙️ Interface for simple SMTP configuration

## 🗄️ Table Of Contents
- [Quick Start](https://github.com/glasskube/operator#-quick-start)
- [Supported Tools](https://github.com/glasskube/operator#-supported-tools)
- [Screencast](https://github.com/glasskube/operator#-screencast)
- [Need help?](https://github.com/glasskube/operator#-need-help)
- [Related projects](https://github.com/glasskube/operator#-related-projects)
- [How to Contribute](https://github.com/glasskube/operator#-how-to-contribute) 
- [Supported by](https://github.com/glasskube/operator#-supported-by)



## 🚀 Quick Start
The Glasskube operator is simply deployed via Helm. To install the Open Source Tool simply apply the Custom Resoure. You can find more information and guidance in our [Docs](https://glasskube.eu/docs/). 

First, add our helm repository and refresh your repository list: 

```console
helm repo add glasskube https://charts.glasskube.eu/
helm repo update
```

Next, install the Glasskube Operator with helm:

```
helm install my-glasskube-operator glasskube/glasskube-operator
```

Install the tool of your choice, for example Gitlab:

```
kubectl apply -f gitlab.yaml
```


## 🔨 Supported Tools 
- Gitea [`go-gitea/gitea`](https://github.com/go-gitea/gitea)
- GitLab [`gitlab.com/gitlab-org/gitlab`](https://gitlab.com/gitlab-org/gitlab)
- GlitchTip [`gitlab.com/glitchtip/glitchtip`](https://gitlab.com/glitchtip)
- Keycloak [`keycloak/keycloak`](https://github.com/keycloak/keycloak)
- Matomo [`matomo-org/matomo`](https://github.com/matomo-org/matomo)
- Metabase [`metabase/metabase`](https://github.com/metabase/metabase)
- Nextcloud [`nextcloud/server`](https://github.com/nextcloud/server)
- Odoo [`odoo/odoo`](https://github.com/odoo/odoo)
- Plane [`makeplane/plane`](https://github.com/makeplane/plane)
- Vault [`hashicorp/vault`](https://github.com/hashicorp/vault)

> Don't find your tool? We are always adding new supported tools, so just join us on [Discord](https://discord.gg/qH6u2nJB) and let us know which tool is missing!

## 🎬 Screencast

https://user-images.githubusercontent.com/3041752/217483828-2f0245bc-dbe8-4fc5-901f-e96131187c22.mp4

## ☝️ Need help?
If you encounter any problems, we will be happy to support you wherever we can. If you encounter any bugs or issues while working on this project, feel free to contact us on [Discord](https://discord.gg/qH6u2nJB). We are happy to assist you with anything related to the project.

## 📎 Related projects

- Java client for Kubernetes [`fabric8io/kubernetes-client`](https://github.com/fabric8io/kubernetes-client)
- Java Operator SDK [`operator-framework/java-operator-sdk`](https://github.com/operator-framework/java-operator-sdk)

## 🤝 How to Contribute

See [the contributing guide](CONTRIBUTING.md) for detailed instructions.

Also join our [`architecture discussion`](https://github.com/glasskube/operator/discussions/4) on GitHub.

## 🤩 Thanks to all our contributors 

Thanks to everyone, that is supporting this project. We are thankful, for evey contribution, no matter its size! 

<a href="https://github.com/glasskube/operator/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=glasskube/operator" />
</a>

## 📘 License 

The Glasskube Operator is licensed under the LGPL-3.0 license. For more information check the [LICENSE](https://github.com/glasskube/operator/blob/main/LICENSE) file for details.

## 🙏 Supported by

- Media Tech Lab [`media-tech-lab`](https://github.com/media-tech-lab)

<a href="https://www.media-lab.de/en/programs/media-tech-lab">
    <img src="https://raw.githubusercontent.com/media-tech-lab/.github/main/assets/mtl-powered-by.png" width="240" title="Media Tech Lab powered by logo">
</a>
