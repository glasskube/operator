---
sidebar_position: 1
toc_max_heading_level: 5
---

# Matomo

Matomo is a robust and privacy-focused web analytics platform designed to help organizations gather valuable insights into their website's performance and visitor behavior.
It stands out for its open-source nature, making it a transparent and customizable choice for analytics.
Matomo offers a comprehensive feature set, making it a dependable alternative to traditional analytics solutions.
This tool empowers businesses to make data-driven decisions while respecting user privacy and data ownership.

## Example

```yaml title=matomo.yaml
apiVersion: glasskube.eu/v1alpha1
kind: Matomo
metadata:
  name: matomo
spec:
  host: stats.mycompany.eu
```

## Spec

| Name      | Type                                                                                                   | Default      |                                                                               |
|-----------|--------------------------------------------------------------------------------------------------------|--------------|-------------------------------------------------------------------------------|
| version   | String                                                                                                 | `"4.15.1.1"` | Check for [releases](https://github.com/glasskube/images/releases) on GitHub. |
| host      | String                                                                                                 | (required)   |                                                                               |
| smtp      | [SmtpSpec](./../common/smtp/)?                                                                         | `null`       |                                                                               |
| resources | [ResourceRequirements](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/) |              |                                                                               |
| database  | [MariaDBSpec](#mariadbspec)?                                                                           |              |                                                                               | 

### MariaDBSpec

| Name    | Type                                      | Default |
|---------|-------------------------------------------|---------|
| storage | [MariaDBStorageSpec](#mariadbstoragespec) |         |

#### MariaDBStorageSpec

| Name         | Type    | Default                                          |
|--------------|---------|--------------------------------------------------|
| size         | String? | `"10Gi"`                                         |
| storageClass | String? | taken from [settings](/getting-started/settings) |
