---
sidebar_position: 1
---

# Gitea

:::note TODO
Add a tool-specific blurb here
:::

## Spec

| Name                | Type                                                                                                                    | Default      |                                                                                                                                                                |
|---------------------|-------------------------------------------------------------------------------------------------------------------------|--------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| host                | String                                                                                                                  | (required)   |                                                                                                                                                                |
| sshEnabled          | Boolean                                                                                                                 | `true`       |                                                                                                                                                                |
| sshHost             | String                                                                                                                  | same as host |                                                                                                                                                                |
| adminSecret         | [LocalObjectReference](https://kubernetes.io/docs/reference/kubernetes-api/common-definitions/local-object-reference/)? | `null`       | Secret containing data of the admin user to create on pod initialization. Expected keys are `GITEA_ADMIN_USER`, `GITEA_ADMIN_EMAIL` and `GITEA_ADMIN_PASSWORD` |
| registrationEnabled | Boolean                                                                                                                 | `false`      |                                                                                                                                                                |
| replicas            | Int                                                                                                                     | `1`          |                                                                                                                                                                |
| smtp                | [SmtpSpec](common/smtp)?                                                                                                | `null`       |                                                                                                                                                                |
| resources           | [ResourceRequirements](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/)                  |              |                                                                                                                                                                |
