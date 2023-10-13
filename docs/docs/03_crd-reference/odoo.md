---
sidebar_position: 1
---

# Odoo

[Odoo](https://github.com/odoo/odoo) is an Open-Source suite of web based business apps written in Python and stores
data in a PostgreSQL database. The Glasskube Operator will automatically perform Upgrades and manages the database. Make
sure you also have the [cnpg-operator](https://github.com/cloudnative-pg/cloudnative-pg) installed. After applying the
custom resource Odoo will be reachable via an ingress at the configured host. Daily database backups are stored in an
integrated S3 compatible MinIO bucket inside the glasskube-system namespace.

## Example

```yaml title=odoo.yaml
apiVersion: glasskube.eu/v1alpha1
kind: Odoo
metadata:
  name: odoo
spec:
  host: erp.mycompany.eu
```

## Spec

| Name        | Type                                                                                                   |                   |                                                                               |
|-------------|--------------------------------------------------------------------------------------------------------|-------------------|-------------------------------------------------------------------------------|
| version     | String                                                                                                 | `"16.0.20230901"` | Check for [releases](https://github.com/glasskube/images/releases) on GitHub. |
| host        | String                                                                                                 | (required)        |                                                                               |
| demoEnabled | Boolean                                                                                                | `true`            |                                                                               |
| resources   | [ResourceRequirements](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/) |                   |                                                                               |
