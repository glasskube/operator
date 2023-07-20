package eu.glasskube.operator.apps.glitchtip.dependent

import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.operator.apps.glitchtip.Glitchtip
import eu.glasskube.operator.apps.glitchtip.Glitchtip.Redis.redisName
import eu.glasskube.operator.apps.glitchtip.GlitchtipReconciler
import eu.glasskube.operator.generic.dependent.redis.RedisService
import io.fabric8.kubernetes.api.model.Service
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(
    labelSelector = GlitchtipReconciler.REDIS_SELECTOR,
    resourceDiscriminator = GlitchtipRedisDeployment.Discriminator::class
)
class GlitchtipRedisService : RedisService<Glitchtip>() {

    internal class Discriminator : ResourceIDMatcherDiscriminator<Service, Glitchtip>({
        ResourceID(it.redisName, it.namespace)
    })

    override val redisNameMapper = Glitchtip.Redis
}
