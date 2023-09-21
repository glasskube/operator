package eu.glasskube.operator.generic.dependent.redis

import eu.glasskube.kubernetes.api.model.intOrString
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.kubernetes.api.model.service
import eu.glasskube.kubernetes.api.model.servicePort
import eu.glasskube.kubernetes.api.model.spec
import io.fabric8.kubernetes.api.model.HasMetadata
import io.fabric8.kubernetes.api.model.Service
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource

abstract class RedisService<T : HasMetadata> :
    RedisDependentResource<T>, CRUDKubernetesDependentResource<Service, T>(Service::class.java) {

    override fun desired(primary: T, context: Context<T>) = service {
        metadata {
            name = redisNameMapper.getName(primary)
            namespace = primary.namespace
            labels = redisNameMapper.getLabels(primary)
        }
        spec {
            selector = redisNameMapper.getLabelSelector(primary)
            ports = listOf(
                servicePort {
                    port = 6379
                    targetPort = intOrString(6379)
                }
            )
        }
    }
}
