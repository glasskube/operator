# Oidc

The Glasskube operator can configure the [`oidc_login`](https://apps.nextcloud.com/apps/oidc_login) nextcloud app for you. Currently, the operator only passes the most basic configuration options throuh.

## Example

```yaml title=spec.apps.office
    oidc:
      name: my-oidc-issuer
      issuerUrl: https://my-oidc-issuer.org
      oidcSecret:
        name: oidc-login
```

## Spec

| Name       | Type                                                                                                                   |            |
|------------|------------------------------------------------------------------------------------------------------------------------|------------|
| name       | String                                                                                                                 | (required) |
| issuerUrl  | String                                                                                                                 | (required) |
| oidcSecret | [LocalObjectReference](https://kubernetes.io/docs/reference/kubernetes-api/common-definitions/local-object-reference/) | (required) |
