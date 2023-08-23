---
sidebar_position: 1
---

# Vault

:::note TODO
Add a tool-specific blurb here
:::

## Spec

| Name                | Type                                                                                                   |            |                                                                                                            |
|---------------------|--------------------------------------------------------------------------------------------------------|------------|------------------------------------------------------------------------------------------------------------|
| host                | String                                                                                                 | (required) |                                                                                                            |
| replicas            | Int                                                                                                    | 3          |                                                                                                            |
| ui                  | [UiSpec](#uispec)                                                                                      |            |                                                                                                            |
| resources           | [ResourceRequirements](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/) |            |                                                                                                            |
| serviceRegistration | [ServiceRegistrationSpec](#serviceregistrationspec)                                                    |            |                                                                                                            |
| autoUnsealSpec      | [AutoUnsealSpec](#autounsealspec)?                                                                     | `null`     | If set to `null`, auto-unsealing is disabled and the vault must be unsealed manually or by external means. |
| auditStorage        | [AuditStorageSpec](#auditstoragespec)                                                                  |            |                                                                                                            |

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
