package eu.glasskube.operator.apps.gitea.dependent

import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.operator.apps.gitea.Gitea
import eu.glasskube.operator.apps.gitea.Gitea.Redis.redisName
import eu.glasskube.operator.generic.dependent.redis.RedisService
import io.fabric8.kubernetes.api.model.Service
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(resourceDiscriminator = GiteaRedisService.Discriminator::class)
class GiteaRedisService : RedisService<Gitea>() {
    override val redisNameMapper = Gitea.Redis

    internal class Discriminator : ResourceIDMatcherDiscriminator<Service, Gitea>({
        ResourceID(it.redisName, it.namespace)
    })
}
