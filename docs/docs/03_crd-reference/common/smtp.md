# SMTP Spec

Configuring SMTP servers for applications is often time-consuming and cumbersome. Glasskube abstracts these configurations
and provides a simple, common interface for supported applications. [Brevo](https://www.brevo.com/) has a generous free plan
and provides an SMTP relay service that has been successfully tested.

Credentials need to be stored in a Secret. We recommend Vault or Sealed Secrets to handle Secrets for you.

## Example

```yaml title=smtp-secret.yaml
apiVersion: v1
kind: Secret
metadata:
  name: smtp-secret
stringData:
  username: "username"
  password: "password"
```


```yaml title=spec.smtp
  smtp:
    host: smtp-relay.brevo.com
    port: 587
    fromAddress: noreply@mycompany.eu
    authSecret:
      name: smtp-secret
    tlsEnabled: true
```


| Name        | Type                                                                                                                   | Default    |
|-------------|------------------------------------------------------------------------------------------------------------------------|------------|
| host        | String                                                                                                                 | (required) |
| port        | Int                                                                                                                    | `587`      |
| fromAddress | String                                                                                                                 | (required) |
| authSecret  | [LocalObjectReference](https://kubernetes.io/docs/reference/kubernetes-api/common-definitions/local-object-reference/) | (required) |
| tlsEnabled  | Boolean                                                                                                                | `true`     |
