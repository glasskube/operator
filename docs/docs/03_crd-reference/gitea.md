---
sidebar_position: 1
---

# Gitea

Gitea is a user-friendly, self-hosted Git service designed for managing code repositories, collaborating on projects, and automating workflows. 
It includes essential features like issue tracking, pull requests, and code reviews, making it a comprehensive platform for software project management. 
Gitea is often considered a lightweight and cost-efficient alternative to GitLab.
While it may offer fewer functionalities than GitLab, it's perfect for small teams seeking a simpler and budget-friendly solution for their development needs.

## Example

```yaml title=gitea.yaml
apiVersion: glasskube.eu/v1alpha1
kind: Gitea
metadata:
  name: gitea
spec:
  host: git.mycompany.eu
  sshHost: ssh.git.mycompany.eu
  registrationEnabled: true
```

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
