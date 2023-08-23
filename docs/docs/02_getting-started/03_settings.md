# Settings

Some aspects of the operator's behavior are controlled via the `glasskube-settings` *ConfigMap*.
On launch, the operator creates this *ConfigMap* with some default values if it does not exist.

The following settings items are currently supported:

| Key                           | Type                             |                                                                                                                                                                                                                     |
|-------------------------------|----------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| cloudProvider                 | Enum: `aws`, `hcloud`, `generic` | Used to control vendor specific annotations, e.g. on *Ingress* and *Service* resources. Determined by the operator on first run.                                                                                    |
| ingressClassName              | String                           | Name of the *IngressClass* the operator should use. This is only necessary if there are multiple *IngressClasses* and no default *IngressClass*.                                                                    |
| databaseStorageClassName      | String                           | Can be used to specify a particular *StorageClass* the operator should use when provisioning database storage. Determined by the operator on first run. It is not recommended to change this once apps are running. |
| commonIngressAnnotations      | Map&lt;String,&nbsp;String>      | Annotations that should be added to all *Ingress* resources created by the operator. Limited support for string templates is available[^1].                                                                         |
| commonLoadBalancerAnnotations | Map&lt;String,&nbsp;String>      | Annotations that should be added to all *Service* resources with type LoadBalancer created by the operator. Limited support for string templates is available[^1].                                                  |

[^1]: `${primary.metadata.name}` and `${primary.metadata.namespace}` will be substituted with the name and namespace of
the primary resource respectively.   
