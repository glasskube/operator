---
sidebar_position: 1
---

# Matomo

[Matomo](https://github.com/matomo-org/matomo) is an Open-Source Web Analytics Tool written in PHP and stores data in
MySQL database. The Glasskube Operator will automatically perform Upgrades and manages the database. Make sure you also
have the [mariadb-operator](https://github.com/mariadb-operator/mariadb-operator) installed. After applying the custom
resource Matomo will be reachable via an ingress at the configured host.

## Example

```yaml title=matomo.yaml
apiVersion: glasskube.eu/v1alpha1
kind: Matomo
metadata:
  name: matomo
spec:
  host: analytics.mycompany.eu
```

## Spec

| Name      | Type                                                                                                   | Default    |
|-----------|--------------------------------------------------------------------------------------------------------|------------|
| host      | String                                                                                                 | (required) |
| smtp      | [SmtpSpec](common/smtp)?                                                                               | `null`     |
| resources | [ResourceRequirements](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/) |            |