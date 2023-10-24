package eu.glasskube.operator.apps.plane.dependent

import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.operator.apps.plane.Plane
import eu.glasskube.operator.apps.plane.Plane.Redis.redisName
import eu.glasskube.operator.generic.dependent.redis.RedisService
import io.fabric8.kubernetes.api.model.Service
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(resourceDiscriminator = PlaneRedisService.Discriminator::class)
class PlaneRedisService : RedisService<Plane>() {

    internal class Discriminator :
        ResourceIDMatcherDiscriminator<Service, Plane>({ ResourceID(it.redisName, it.namespace) })

    override val redisNameMapper = Plane.Redis
}
