---
sidebar_position: 1
---

# Keycloak

Keycloak is an open-source Identity and Access Management platform that simplifies user authentication, offers user federation with various data sources,
and supports standard security protocols like OpenID Connect, OAuth 2.0, and SAML. 
It includes an admin console for centralized management of user permissions, sessions, and application configurations.

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
