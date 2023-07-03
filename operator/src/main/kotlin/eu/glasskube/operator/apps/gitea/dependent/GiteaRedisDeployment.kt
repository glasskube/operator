package eu.glasskube.operator.apps.gitea.dependent

import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.operator.apps.gitea.Gitea
import eu.glasskube.operator.apps.gitea.Gitea.Redis.redisName
import eu.glasskube.operator.apps.gitea.GiteaReconciler
import eu.glasskube.operator.generic.dependent.redis.RedisDeployment
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(
    labelSelector = GiteaReconciler.REDIS_SELECTOR,
    resourceDiscriminator = GiteaRedisDeployment.Discriminator::class
)
class GiteaRedisDeployment : RedisDeployment<Gitea>() {
    override val redisNameMapper = Gitea.Redis

    internal class Discriminator : ResourceIDMatcherDiscriminator<Deployment, Gitea>({
        ResourceID(it.redisName, it.namespace)
    })
}
