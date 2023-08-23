---
sidebar_position: 1
---

# Metabase

:::note TODO
Add a tool-specific blurb here
:::

## Spec

| Name      | Type                                                                                                   | Default    |
|-----------|--------------------------------------------------------------------------------------------------------|------------|
| host      | String                                                                                                 | (required) |
| replicas  | Int                                                                                                    | 1          |
| smtp      | [SmtpSpec](common/smtp)?                                                                                              | `null`     |
| resources | [ResourceRequirements](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/) |            |