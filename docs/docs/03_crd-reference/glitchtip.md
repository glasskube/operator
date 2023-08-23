---
sidebar_position: 1
---

# Glitchtip

:::note TODO
Add a tool-specific blurb here
:::

## Spec

| Name                        | Type                                                                                                   | Default    |
|-----------------------------|--------------------------------------------------------------------------------------------------------|------------|
| host                        | String                                                                                                 | (required) |  
| replicas                    | Int                                                                                                    | `1`        | 
| registrationEnabled         | Boolean                                                                                                | `false`    |
| organizationCreationEnabled | Boolean                                                                                                | `false`    |
| smtp                        | [SmtpSpec](common/smtp)?                                                                                              | `null`     | 
| resources                   | [ResourceRequirements](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/) |            |
