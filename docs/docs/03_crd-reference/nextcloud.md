---
toc_max_heading_level: 5
sidebar_position: 1
---

# Nextcloud

:::note TODO
Add a tool-specific blurb here
:::

## Spec

| Name               | Type                                                                                                   |            |
|--------------------|--------------------------------------------------------------------------------------------------------|------------|
| host               | String                                                                                                 | (required) |
| defaultPhoneRegion | String?                                                                                                | `null`     |
| apps               | [AppsSpec](#apps)                                                                                      |            |
| resources          | [ResourceRequirements](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/) |            |
| smtp               | [SmtpSpec](common/smtp)?                                                                                              | `null`     |
| storage            | [StorageSpec](#storage)                                                                                | `null`     |

### AppsSpec {#apps}

| Name   | Type                  |        |
|--------|-----------------------|--------|
| office | [OfficeSpec](#office) | `null` |

#### OfficeSpec {#office}

| Name | Type   |            |
|------|--------|------------|
| host | String | (required) |

### StorageSpec {#storage}

| Name | Type      |            |
|------|-----------|------------|
| s3   | [S3](#s3) | (required) |

#### S3

| Name            | Type              |            |
|-----------------|-------------------|------------|
| bucket          | String            | (required) |
| accessKeySecret | SecretKeySelector | (required) |
| secretKeySecret | SecretKeySelector | (required) |
| region          | String?           | `null`     |
| hostname        | String?           | `null`     |
| port            | Int?              | `null`     |
| objectPrefix    | String?           | `null`     |
| autoCreate      | Boolean?          | `null`     |
| useSsl          | Boolean           | `true`     |
| usePathStyle    | Boolean?          | `null`     |
| legacyAuth      | Boolean?          | `null`     |