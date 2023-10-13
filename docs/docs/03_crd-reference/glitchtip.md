---
sidebar_position: 1
---

# GlitchTip

GlitchTip is a simplified software monitoring platform that centralizes error tracking, application performance monitoring, and website uptime checks.
It seamlessly integrates with Sentry client SDKs, streamlining error collection and resolution.
GlitchTip offers straightforward performance insights and the ability to monitor website uptime, providing a comprehensive solution for application health.

## Example

```yaml title=glitchtip.yaml
apiVersion: glasskube.eu/v1alpha1
kind: Glitchtip
metadata:
  name: glitchtip
spec:
  host: glitchtip.mycompany.eu
  registrationEnabled: false
  organizationCreationEnabled: false
```

## Spec

| Name                        | Type                                                                                                   | Default    |                                                                                            |
|-----------------------------|--------------------------------------------------------------------------------------------------------|------------|--------------------------------------------------------------------------------------------|
| version                     | String                                                                                                 | `"3.3.1"`  | Check for [releases](https://gitlab.com/glitchtip/glitchtip-backend/-/releases) on GitLab. |
| host                        | String                                                                                                 | (required) |                                                                                            |
| replicas                    | Int                                                                                                    | `1`        |                                                                                            |
| registrationEnabled         | Boolean                                                                                                | `false`    |                                                                                            |
| organizationCreationEnabled | Boolean                                                                                                | `false`    |                                                                                            |
| smtp                        | [SmtpSpec](./../common/smtp/)?                                                                         | `null`     |                                                                                            |
| resources                   | [ResourceRequirements](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/) |            |
| database                    | [PostgresDatabaseSpec](./../common/postgres)?                                                          |            |                                                                                            |
