---
toc_max_heading_level: 5
---

# Postgres Database

## Spec

| Name      | Type                         | Default |                                                                                                                |
|-----------|------------------------------|---------|----------------------------------------------------------------------------------------------------------------|
| instances | Int                          | `1`     | For performance and reliability reasons, we recommend to use at least 2 replicas for production installations. |
| storage   | [StorageSpec](#storagespec)? |         |                                                                                                                |
| backups   | [BackupsSpec](#backupsspec)? |         |                                                                                                                |

### StorageSpec

| Name         | Type    | Default                                          |
|--------------|---------|--------------------------------------------------|
| size         | String? | depends on app                                   |
| storageClass | String? | taken from [settings](/getting-started/settings) |

### BackupsSpec

| Name            | Type              | Default         |
|-----------------|-------------------|-----------------|
| schedule        | String            | `"0 0 3 * * *"` |
| retentionPolicy | String?           | `null`          |
| s3              | [S3Spec](#s3spec) | (required)      |

#### S3Spec

| Name            | Type               | Default    |
|-----------------|--------------------|------------|
| endpoint        | String?            | `null`     |
| regionSecret    | SecretKeySelector? | `null`     |
| bucket          | String             | (required) |
| accessKeySecret | SecretKeySelector  | (required) |
| secretKeySecret | SecretKeySelector  | (required) |

