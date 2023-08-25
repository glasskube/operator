---
sidebar_position: 1
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

| Name      | Type                                                                                                   | Default    |
|-----------|--------------------------------------------------------------------------------------------------------|------------|
| host      | String                                                                                                 | (required) |
| smtp      | [SmtpSpec](common/smtp)?                                                                               | `null`     |
| resources | [ResourceRequirements](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/) |            |
