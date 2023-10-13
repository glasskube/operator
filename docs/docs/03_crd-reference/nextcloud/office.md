# Office

The Glasskube operator can configure the Collabora Online Office app by adding the office section into the apps list.

## Example

```yaml title=spec.apps.office
    office:
      host: office.nextcloud.mycompany.eu
```

## Spec

| Name    | Type   |                 |                                                                                     |
|---------|--------|-----------------|-------------------------------------------------------------------------------------|
| version | String | `"23.05.2.2.1"` | Check for [releases](https://github.com/CollaboraOnline/online/releases) on GitHub. |
| host    | String | (required)      |                                                                                     |
