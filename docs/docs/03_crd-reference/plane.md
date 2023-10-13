---
sidebar_position: 1
---

# Plane

[Plane](https://plane.so/) is an Open-Source project management solution composed of several backend components written in Python and using PostgreSQL for persistence and frontend components using React/Next.js.
It allows you to start simple and gradually embrace different established project management workflows.
One instance can be used by several teams, as Plane allows you to create multiple [workspaces](https://docs.plane.so/workspaces), and [projects](https://docs.plane.so/projects).

## Example

```yaml plane.yaml
apiVersion: glasskube.eu/v1alpha1
kind: Plane
metadata:
  name: plane
spec:
  host: issues.mycompany.eu
```

## Spec

| Name                | Type                                          | Default                                                       |                                                                                                                    |
|---------------------|-----------------------------------------------|---------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------|
| version             | String                                        | `"v0.12.2-dev"`                                               | Check for [releases](https://github.com/makeplane/plane/releases) on GitHub.                                       |
| host                | String                                        | (required)                                                    |                                                                                                                    |
| registrationEnabled | Boolean                                       | `true`                                                        | If set to `false`, the registration page is still accessible, but trying to register an account leads to an error. |
| defaultUser         | [DefaultUserSpec](#defaultuserspec)           | email: `"root@example.com"`, password: `"glasskube-operator"` | We strongly recommend that you change the initial user password immediately after you first sign in.               |
| frontend            | [FrontendSpec](#frontendspec)                 |                                                               |                                                                                                                    |
| space               | [SpaceSpec](#spacespec)                       |                                                               |                                                                                                                    |
| api                 | [ApiSpec](#apispec)                           |                                                               |                                                                                                                    |
| beatWorker          | [BeatWorkerSpec](#beatworkerspec)             |                                                               |                                                                                                                    |
| worker              | [WorkerSpec](#workerspec)                     |                                                               |                                                                                                                    |
| smtp                | [SmtpSpec](./../common/smtp/)?                | `null`                                                        |                                                                                                                    |
| s3                  | [S3Spec](#s3spec)?                            | `null`                                                        | Required for issue attachments.                                                                                    |
| database            | [PostgresDatabaseSpec](./../common/postgres)? |                                                               |                                                                                                                    |

## DefaultUserSpec

| Name     | Type   | Default    |
|----------|--------|------------|
| email    | String | (required) |
| password | String | (required) |

## FrontendSpec

| Name      | Type                                                                                                    | Default |
|-----------|---------------------------------------------------------------------------------------------------------|---------|
| resources | [ResourceRequirements](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/)? |         |

## SpaceSpec

| Name      | Type                                                                                                    | Default |
|-----------|---------------------------------------------------------------------------------------------------------|---------|
| resources | [ResourceRequirements](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/)? |         |

## ApiSpec

| Name        | Type                                                                                                    | Default |                                                                                                                          |
|-------------|---------------------------------------------------------------------------------------------------------|---------|--------------------------------------------------------------------------------------------------------------------------|
| concurrency | Int                                                                                                     | `2`     | Low values may negatively affect site performance but resource usage increases linearly with the value of `concurrency`. |
| resources   | [ResourceRequirements](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/)? |         | The default value depends on `concurrency`.                                                                              |

## BeatWorkerSpec

| Name      | Type                                                                                                    | Default |
|-----------|---------------------------------------------------------------------------------------------------------|---------|
| resources | [ResourceRequirements](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/)? |         |

## WorkerSpec

| Name        | Type                                                                                                    | Default |                                                                    |
|-------------|---------------------------------------------------------------------------------------------------------|---------|--------------------------------------------------------------------|
| concurrency | Int                                                                                                     | `2`     | Resource usage increases linearly with the value of `concurrency`. |
| resources   | [ResourceRequirements](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/)? |         | The default value depends on `concurrency`.                        |

## S3Spec

| Name            | Type              | Default    |                                                                                                                                                                                                                                                            |
|-----------------|-------------------|------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| bucket          | String            | (required) |                                                                                                                                                                                                                                                            |
| accessKeySecret | SecretKeySelector | (required) |                                                                                                                                                                                                                                                            |
| secretKeySecret | SecretKeySelector | (required) |                                                                                                                                                                                                                                                            |
| region          | String            | (required) |                                                                                                                                                                                                                                                            |
| endpoint        | String?           | `null`     |                                                                                                                                                                                                                                                            |
| usePathStyle    | Boolean?          | `null`     | Currently unused. In its current version, Plane automatically selects "virtual host" style if an AWS endpoint is used (the default) and "path style" if any other endpoint is used. This means that your object storage provider must support "path style" |
