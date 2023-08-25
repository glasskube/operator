# Runner

Runners on GitLab are execution agents that power continuous integration and continuous delivery (CI/CD) pipelines. 
They're responsible for running jobs, which are the individual steps or tasks within a pipeline.

```yaml title=spec.runners
  runners:
    - token: glrt-xxxxXX-xxxxxXXXXX # can be generated at https://{{host}}/admin/runners/new
```

## Spec

| Name        | Type                                                                                                                   | Default    |                                                                                                                                      |
|-------------|------------------------------------------------------------------------------------------------------------------------|------------|--------------------------------------------------------------------------------------------------------------------------------------|
| token       | String                                                                                                                 | (required) | Must be generated via GitLab admin UI.                                                                                               |
| gitlab      | [LocalObjectReference](https://kubernetes.io/docs/reference/kubernetes-api/common-definitions/local-object-reference/) | (required) | Name of the GitLab instance the runner should connect to. This must be the same instance that was used to generate the runner token. | 
| concurrency | Int                                                                                                                    | `1`        | The number of pipeline steps the runner is allowed to execute concurrently.                                                          |
https://github.com/safeREACH/cluster/tree/master/com-staging#upgrading-the-cluster
