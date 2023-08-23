---
toc_max_heading_level: 5
---

# Gitlab

:::note TODO
Add a tool-specific blurb here
:::

## Spec

| Name                      | Type                                                                                                   | Default    |                                                |
|---------------------------|--------------------------------------------------------------------------------------------------------|------------|------------------------------------------------|
| host                      | String                                                                                                 | (required) |                                                |
| sshEnabled                | Boolean                                                                                                | `true`     |                                                |
| sshHost                   | String?                                                                                                | `null`     |                                                |
| initialRootPasswordSecret | SecretKeySelector?                                                                                     | `null`     |                                                |
| smtp                      | [SmtpSpec](../common/smtp)?                                                                            | `null`     |                                                | 
| runners                   | List&lt;[RunnerSpecTemplate](#runner-spec-template)>                                                   | `[]`       |                                                |
| resources                 | [ResourceRequirements](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/) |            |                                                |
| omnibusConfigOverride     | String?                                                                                                | `null`     | For advanced users only. Use at your own risk! |
| registry                  | [RegistrySpec](#registry)?                                                                             | `null`     |                                                |

### RunnerSpecTemplate {#runner-spec-template}

Partial specification of a [runner](./runner) that should be started for this instance.

| Name        | Type   | Default    |
|-------------|--------|------------|
| token       | String | (required) |
| concurrency | Int    | `1`        |

### RegistrySpec {#registry}

| Name    | Type                                     | Default    |
|---------|------------------------------------------|------------|
| host    | String                                   | (required) |
| storage | [RegistryStorageSpec](#registry-storage) | `null`     |

#### RegistryStorageSpec {#registry-storage}

| Name | Type      | Default    |
|------|-----------|------------|
| s3   | [S3](#s3) | (required) |                                                

##### S3

| Name            | Type              | Default    |
|-----------------|-------------------|------------|
| bucket          | String            | (required) |                                                
| accessKeySecret | SecretKeySelector | (required) |                                                
| secretKeySecret | SecretKeySelector | (required) |                                                
| region          | String            | (required) |                                                
| hostname        | String            | (required) |                                                
| usePathStyle    | Boolean           | (required) |                                                

