---
sidebar_position: 1
---

# Keycloak

:::note TODO
Add a tool-specific blurb here
:::

## Spec

| Name       | Type                                                                                                   | Default    |                                             | 
|------------|--------------------------------------------------------------------------------------------------------|------------|---------------------------------------------|
| host       | String                                                                                                 | (required) |                                             |
| management | [ManagementSpec](#management)                                                                          |            | Configuration of the keycloak management UI |
| resources  | [ResourceRequirements](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/) |            |                                             |

### ManagementSpec {#management}

| Name    | Type    | Default |                                                          |
|---------|---------|---------|----------------------------------------------------------|
| enabled | Boolean | `true`  | Whether the management UI should be exposed via Ingress. |