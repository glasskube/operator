---
toc_max_heading_level: 5
---

# GitLab

GitLab is a robust platform for software development that provides a range of essential tools for version control,
continuous integration, issue tracking, and more. It serves as a centralized hub for collaborative software development.
GitLab simplifies the development workflow, offering a user-friendly interface and a suite of features that streamline
the software development process.

## Example

```yaml title=gitlab.yaml
apiVersion: glasskube.eu/v1alpha1
kind: Gitlab
metadata:
  name: gitlab
spec:
  host: gitlab.mycompany.eu
  sshEnabled: true
  sshHost: ssh.gitlab.mycompany.eu
  runners: [ ]
  registry:
    host: registry.gitlab.mycompany.eu
    storage:
      s3:
        bucket: gitlab-registry-bucket
        accessKeySecret:
          name: gitlab-registry-bucket-secret
          key: accessKey
        secretKeySecret:
          name: gitlab-registry-bucket-secret
          key: secretKey
        hostname: s3host.yourcompany.eu
        usePathStyle: true
        region: eu
```

## Spec

| Name                      | Type                                                                                                   | Default    |                                                                                       |
|---------------------------|--------------------------------------------------------------------------------------------------------|------------|---------------------------------------------------------------------------------------|
| version                   | String                                                                                                 | `"16.2.5"` | Check for [releases](https://gitlab.com/gitlab-org/gitlab-foss/-/releases) on GitLab. |
| host                      | String                                                                                                 | (required) |                                                                                       |
| sshEnabled                | Boolean                                                                                                | `true`     |                                                                                       |
| sshHost                   | String?                                                                                                | `null`     |                                                                                       |
| initialRootPasswordSecret | SecretKeySelector?                                                                                     | `null`     |                                                                                       |
| smtp                      | [SmtpSpec](../common/smtp)?                                                                            | `null`     |                                                                                       | 
| runners                   | List&lt;[RunnerSpecTemplate](./runner)>                                                                | `[]`       |                                                                                       |
| resources                 | [ResourceRequirements](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/) |            |                                                                                       |
| omnibusConfigOverride     | String?                                                                                                | `null`     | For advanced users only. Use at your own risk!                                        |
| registry                  | [RegistrySpec](#registry)?                                                                             | `null`     |                                                                                       |
| database                  | [PostgresDatabaseSpec](./../common/postgres)?                                                          |            |                                                                                       |

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
| hostname        | String            | `null`     |                                                
| port            | Integer           | `null`     |
| useSsl          | Boolean           | `true`     |
| usePathStyle    | Boolean           | `false`    |
