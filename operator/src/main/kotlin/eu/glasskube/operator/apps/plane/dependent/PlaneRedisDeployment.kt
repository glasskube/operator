package eu.glasskube.operator.apps.plane.dependent

import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.operator.apps.plane.Plane
import eu.glasskube.operator.apps.plane.Plane.Redis.redisName
import eu.glasskube.operator.generic.dependent.redis.RedisDeployment
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(resourceDiscriminator = PlaneRedisDeployment.Discriminator::class)
class PlaneRedisDeployment : RedisDeployment<Plane>() {

    internal class Discriminator :
        ResourceIDMatcherDiscriminator<Deployment, Plane>({ ResourceID(it.redisName, it.namespace) })

    override val redisNameMapper = Plane.Redis
}
