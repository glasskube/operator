---
sidebar_position: 1
---

# Vault

HashiCorp Vault is an open-source tool that serves as a centralized hub for secrets management, encryption services, and privileged access management.
It enhances security by consolidating dynamic secrets, offers robust management within Kubernetes, provides comprehensive data protection features, including encryption and tokenization.
Vault is a perfect fit for organizations implementing zero trust strategies, ensuring heightened security and access control.

## Example

```yaml title=vault.yaml
kind: Vault
metadata:
  name: vault
spec:
  host: vault.mycompany.eu
  auditStorage:
    enabled: true

```

## Spec

| Name                | Type                                                                                                   |            |                                                                                                            |
|---------------------|--------------------------------------------------------------------------------------------------------|------------|------------------------------------------------------------------------------------------------------------|
| version             | String                                                                                                 | `"1.14.2"` | Check for [releases](https://github.com/hashicorp/vault/releases) on GitHub.                               |
| host                | String                                                                                                 | (required) |                                                                                                            |
| replicas            | Int                                                                                                    | 3          |                                                                                                            |
| ui                  | [UiSpec](#uispec)                                                                                      |            |                                                                                                            |
| resources           | [ResourceRequirements](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/) |            |                                                                                                            |
| serviceRegistration | [ServiceRegistrationSpec](#serviceregistrationspec)                                                    |            |                                                                                                            |
| autoUnsealSpec      | [AutoUnsealSpec](#autounsealspec)?                                                                     | `null`     | If set to `null`, auto-unsealing is disabled and the vault must be unsealed manually or by external means. |
| auditStorage        | [AuditStorageSpec](#auditstoragespec)                                                                  |            |                                                                                                            |
| database            | [PostgresDatabaseSpec](./../common/postgres)?                                                          |            |                                                                                                            |

### UiSpec

| Name    | Type    | Default |                                                                                                                                                                    |
|---------|---------|---------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| enabled | Boolean | `true`  | Allows to disable exposure of the vault web UI. It is recommended to disable the web UI if it is not used, to reduce the attack surface of the vault installation. |

### ServiceRegistrationSpec

| Name    | Type    | Default |                                                                                                                                                                                                                          |
|---------|---------|---------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| enabled | Boolean | `true`  | Toggles the kubernetes service registration feature. If set to `true` the vault service account is granted permission to patch pod resources in its own namespace. This is done automatically by the Glasskube Operator. |

### AutoUnsealSpec

| Name        | Type               | Default        |                                                                  |
|-------------|--------------------|----------------|------------------------------------------------------------------|
| address     | String             | (required)     |                                                                  |
| tlsCaSecret | SecretKeySelector? | `null`         |                                                                  |
| authPath    | String             | `"kubernetes"` | Mount path of the kubernetes auth method in the unsealing vault. |
| roleName    | String?            | `null`         | If set to `null`, a value of `"namespace.name"` is assumed.      |
| mountPath   | String             | `"transit"`    |                                                                  |
| keyName     | String?            | `null`         | If set to `null`, a value of `"namespace.name"` is assumed.      |

### AuditStorageSpec

| Name    | Type     | Default |
|---------|----------|---------|
| enabled | Boolean  | `true`  |
| size    | Quantity | `1Gi`   |
