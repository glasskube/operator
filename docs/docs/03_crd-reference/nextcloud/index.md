---
toc_max_heading_level: 5
sidebar_position: 1
---

# Nextcloud

Nextcloud is an open-source platform for content collaboration.
It offers real-time document editing, video chat, and groupware functions accessible across multiple platforms.
Nextcloud facilitates seamless file access, sharing, and synchronization, enhances online dialogues, and provides a user-friendly
office suite for collaborative editing, making it a versatile tool for individuals and organizations.

## Example

```yaml title=nextcloud.yaml
apiVersion: glasskube.eu/v1alpha1
kind: Nextcloud
metadata:
  name: nextcloud
spec:
  host: nextcloud.mycompany.eu
  defaultPhoneRegion: DE
  apps:
    office:
      host: office.nextcloud.mycompany.eu
```

## Spec

| Name               | Type                                        |            |                                                                               |
|--------------------|---------------------------------------------|------------|-------------------------------------------------------------------------------|
| version            | String                                      | `"27.0.1"` | Check for [releases](https://github.com/nextcloud/server/releases) on GitHub. |
| host               | String                                      | (required) |                                                                               |
| defaultPhoneRegion | String?                                     | `null`     |                                                                               |
| apps               | [AppsSpec](#apps)                           |            |                                                                               |
| server             | [ServerSpec](#serverspec)                   |            |                                                                               |
| smtp               | [SmtpSpec](../common/smtp)?                 | `null`     |                                                                               |
| storage            | [StorageSpec](#storage)                     | `null`     |                                                                               |
| database           | [PostgresDatabaseSpec](../common/postgres)? |            |                                                                               |

### AppsSpec {#apps}

| Name   | Type                   |        |
|--------|------------------------|--------|
| office | [OfficeSpec](./office) | `null` |
| oidc   | [OidcSpec](./oidc)     | `null` |

### ServerSpec

| Name            | Type                                                                                                   |                          |
|-----------------|--------------------------------------------------------------------------------------------------------|--------------------------|
| resources       | [ResourceRequirements](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/) |                          |
| maxChildren     | Int                                                                                                    | `512`                    |
| startServers    | Int                                                                                                    | depends on `maxChildren` |
| minSpareServers | Int                                                                                                    | depends on `maxChildren` |
| maxSpareServers | Int                                                                                                    | depends on `maxChildren` |

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
