---
sidebar_position: 1
---

# Vault

:::note TODO
Add a tool-specific blurb here
:::

## Spec

| Name                | Type                                                                                                   |            |
|---------------------|--------------------------------------------------------------------------------------------------------|------------|
| host                | String                                                                                                 | (required) |
| replicas            | Int                                                                                                    | 3          |
| ui                  | [UiSpec](#ui)                                                                                          |            |
| resources           | [ResourceRequirements](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/) |            |
| serviceRegistration | [ServiceRegistrationSpec](#service-registration)                                                       |            |
| autoUnsealSpec      | [AutoUnsealSpec](#auto-unseal)?                                                                        | `null`     |
| auditStorage        | [AuditStorageSpec](#audit-storage)                                                                     |            |

### UiSpec {#ui}

| Name    | Type    | Default |
|---------|---------|---------|
| enabled | Boolean | `true`  |

### ServiceRegistrationSpec {#service-registration}

| Name    | Type    | Default |
|---------|---------|---------|
| enabled | Boolean | `true`  |

### AutoUnsealSpec {#auto-unseal}

| Name        | Type               | Default     |                                                             |
|-------------|--------------------|-------------|-------------------------------------------------------------|
| address     | String             | (required)  |                                                             |
| tlsCaSecret | SecretKeySelector? | `null`      |                                                             |
| roleName    | String?            | `null`      | If set to `null`, a value of `"namespace.name"` is assumed. |
| mountPath   | String             | `"transit"` |                                                             |
| keyName     | String?            | `null`      | If set to `null`, a value of `"namespace.name"` is assumed. |

### AuditStorageSpec {#audit-storage}

| Name    | Type     | Default |
|---------|----------|---------|
| enabled | Boolean  | `true`  |
| size    | Quantity | `1Gi`   |