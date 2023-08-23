# SMTP Spec

| Name        | Type                                                                                                                   | Default    |  |
|-------------|------------------------------------------------------------------------------------------------------------------------|------------|--|
| host        | String                                                                                                                 | (required) |  |
| port        | Int                                                                                                                    | `587`      |  |
| fromAddress | String                                                                                                                 | (required) |  |
| authSecret  | [LocalObjectReference](https://kubernetes.io/docs/reference/kubernetes-api/common-definitions/local-object-reference/) | (required) |  |
| tlsEnabled  | Boolean                                                                                                                | `true`     |  |
