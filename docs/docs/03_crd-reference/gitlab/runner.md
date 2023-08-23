# Gitlab Runner

Gitlab runners are necessary for the execution of CI/CD pipelines on Gitlab.

## Spec

| Name        | Type                                                                                                                   | Default    |                                                                                                                                      |
|-------------|------------------------------------------------------------------------------------------------------------------------|------------|--------------------------------------------------------------------------------------------------------------------------------------|
| token       | String                                                                                                                 | (required) | Must be generated via Gitlab admin UI.                                                                                               |
| gitlab      | [LocalObjectReference](https://kubernetes.io/docs/reference/kubernetes-api/common-definitions/local-object-reference/) | (required) | Name of the Gitlab instance the runner should connect to. This must be the same instance that was used to generate the runner token. | 
| concurrency | Int                                                                                                                    | `1`        | The number of pipeline steps the runner is allowed to execute concurrently.                                                          |
