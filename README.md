# Glasskube Operator

## About

The Glasskube Operator deploys open source tools on Kubernetes and keeps them running.
It also sets up the databases and caches those tools need and keeps everything up to date.

Each tool has its own Kubernetes [custom resource definition](https://kubernetes.io/docs/concepts/extend-kubernetes/api-extension/custom-resources/) (CRD), and you configure the tool through that resource.
The operator prefers good defaults over a long list of options.
For Gitea, for example, the only required field is the hostname.

## Features

- Installation with little or no configuration
- Databases and caches are set up automatically
- Version updates and patches can be scheduled
- Scheduled database backups
- One SMTP configuration format for every supported tool

## Contents

- [Quick start](#quick-start)
- [Dependencies](#dependencies)
- [Settings](#settings)
- [Supported tools](#supported-tools)
- [Getting help](#getting-help)
- [Related projects](#related-projects)
- [Contributing](#contributing)
- [Supported by](#supported-by)

## Quick start

You need a Kubernetes cluster, a clone of this repository, and the `kubectl` and `helm` CLIs.
[`minikube`](https://minikube.sigs.k8s.io/) is enough to try things out.

[`deploy/deploy.sh`](deploy/deploy.sh) installs the CRDs, all [dependencies](#dependencies) and the operator.
Pick a version from [Docker Hub](https://hub.docker.com/r/glasskube/operator/tags) and run:

```console
./deploy/deploy.sh -v <version>
```

This installs everything cluster wide. Add `-n <namespace>` to install into a single namespace instead, and run `./deploy/deploy.sh -h` to see the other options.

Then create a resource for the tool you want. This one installs Gitea:

```yaml
# gitea.yaml
apiVersion: glasskube.eu/v1alpha1
kind: Gitea
metadata:
  name: gitea
spec:
  host: git.mycompany.eu
  sshHost: ssh.git.mycompany.eu
  registrationEnabled: true
```

```
kubectl apply -f gitea.yaml
```

The specs for all resources are defined in [`operator/src/main/kotlin/eu/glasskube/operator/apps`](operator/src/main/kotlin/eu/glasskube/operator/apps).

## Dependencies

The operator hands some work to other operators, which have to be installed in your cluster.
`deploy/deploy.sh` installs all of them with Helm.
Running it without `-v` installs only the dependencies.

| Dependency                                                               | Needed by            | What the operator uses it for                                                                              |
|--------------------------------------------------------------------------|----------------------|------------------------------------------------------------------------------------------------------------|
| [cert-manager](https://cert-manager.io/)                                 | all tools (optional) | TLS certificates for the *Ingress* resources it creates. Without cert-manager, the operator's *Ingress* annotations are ignored. |
| [Prometheus Operator](https://prometheus-operator.dev/)                  | all tools            | *ServiceMonitor* and *PodMonitor* resources                                                                |
| [CloudnativePG](https://cloudnative-pg.io/)                              | all tools            | PostgreSQL instances                                                                                       |
| [MinIO](https://min.io/)                                                 | all tools            | Storage for database WALs and backups. **Deprecated**, see [#715](https://github.com/glasskube/operator/issues/715).                                                                      |
| [MariaDB Operator](https://github.com/mariadb-operator/mariadb-operator) | Matomo               | MariaDB instances                                                                                          |

## Settings

The `glasskube-settings` *ConfigMap* controls parts of the operator's behavior.
If it doesn't exist at startup, the operator creates it with default values.

| Key                             | Type                             | Description                                                                                                         |
|---------------------------------|----------------------------------|---------------------------------------------------------------------------------------------------------------------|
| `cloudProvider`                 | Enum: `aws`, `hcloud`, `generic` | Picks provider specific annotations, for example on *Ingress* and *Service* resources. Detected on first run.       |
| `ingressClassName`              | String                           | *IngressClass* to use. You only need this if the cluster has several *IngressClasses* and none of them is default. |
| `databaseStorageClassName`      | String                           | *StorageClass* for database volumes. Detected on first run. Don't change it once apps are running.                 |
| `commonIngressAnnotations`      | Map&lt;String,&nbsp;String>      | Annotations added to every *Ingress* the operator creates.                                                          |
| `commonLoadBalancerAnnotations` | Map&lt;String,&nbsp;String>      | Annotations added to every *Service* of type LoadBalancer the operator creates.                                     |

In both annotation settings, `${primary.metadata.name}` and `${primary.metadata.namespace}` are replaced with the name and namespace of the custom resource.

## Supported tools

- Gitea [`go-gitea/gitea`](https://github.com/go-gitea/gitea)
- GitLab [`gitlab.com/gitlab-org/gitlab`](https://gitlab.com/gitlab-org/gitlab)
- GlitchTip [`gitlab.com/glitchtip/glitchtip`](https://gitlab.com/glitchtip) **(deprecated, see [#714](https://github.com/glasskube/operator/issues/714))**
- Keycloak [`keycloak/keycloak`](https://github.com/keycloak/keycloak)
- Matomo [`matomo-org/matomo`](https://github.com/matomo-org/matomo)
- Metabase [`metabase/metabase`](https://github.com/metabase/metabase)
- Nextcloud [`nextcloud/server`](https://github.com/nextcloud/server)
- Odoo [`odoo/odoo`](https://github.com/odoo/odoo) **(deprecated, see [#714](https://github.com/glasskube/operator/issues/714))**
- Plane [`makeplane/plane`](https://github.com/makeplane/plane) **(deprecated, see [#714](https://github.com/glasskube/operator/issues/714))**
- Vault [`hashicorp/vault`](https://github.com/hashicorp/vault)

If the tool you need isn't listed, [open an issue](https://github.com/glasskube/operator/issues/new).

## Getting help

If something doesn't work or you found a bug, [open an issue](https://github.com/glasskube/operator/issues/new).

## Related projects

- Java client for Kubernetes [`fabric8io/kubernetes-client`](https://github.com/fabric8io/kubernetes-client)
- Java Operator SDK [`operator-framework/java-operator-sdk`](https://github.com/operator-framework/java-operator-sdk)

## Contributing

See [the contributing guide](CONTRIBUTING.md).
Architecture questions are discussed in [this GitHub discussion](https://github.com/glasskube/operator/discussions/4).

## License

The Glasskube Operator is licensed under LGPL-3.0. See the [LICENSE](LICENSE) file for details.

## Supported by

- Media Tech Lab [`media-tech-lab`](https://github.com/media-tech-lab)

<a href="https://www.media-lab.de/en/programs/media-tech-lab">
    <img src="https://raw.githubusercontent.com/media-tech-lab/.github/main/assets/mtl-powered-by.png" width="240" title="Media Tech Lab powered by logo">
</a>
