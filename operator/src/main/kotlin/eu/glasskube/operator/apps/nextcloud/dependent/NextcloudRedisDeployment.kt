package eu.glasskube.operator.apps.nextcloud.dependent

import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.operator.apps.nextcloud.Nextcloud
import eu.glasskube.operator.apps.nextcloud.Nextcloud.Redis.redisName
import eu.glasskube.operator.generic.dependent.redis.RedisDeployment
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(resourceDiscriminator = NextcloudRedisDeployment.Discriminator::class)
class NextcloudRedisDeployment : RedisDeployment<Nextcloud>() {
    internal class Discriminator : ResourceIDMatcherDiscriminator<Deployment, Nextcloud>({
        ResourceID(it.redisName, it.namespace)
    })

    override val redisNameMapper = Nextcloud.Redis
}
