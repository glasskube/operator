package eu.glasskube.operator.gitea.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.service
import eu.glasskube.kubernetes.api.model.servicePort
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.operator.gitea.Gitea
import eu.glasskube.operator.gitea.GiteaReconciler
import eu.glasskube.operator.gitea.redisLabelSelector
import eu.glasskube.operator.gitea.redisLabels
import eu.glasskube.operator.gitea.redisName
import io.fabric8.kubernetes.api.model.IntOrString
import io.fabric8.kubernetes.api.model.Service
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(
    labelSelector = GiteaReconciler.REDIS_SELECTOR,
    resourceDiscriminator = GiteaRedisService.Discriminator::class
)
class GiteaRedisService : CRUDKubernetesDependentResource<Service, Gitea>(Service::class.java) {
    internal class Discriminator : ResourceIDMatcherDiscriminator<Service, Gitea>({ ResourceID(it.redisName) })

    override fun desired(primary: Gitea, context: Context<Gitea>) = service {
        metadata {
            name = primary.redisName
            namespace = primary.metadata.namespace
            labels = primary.redisLabels
        }
        spec {
            selector = primary.redisLabelSelector
            ports = listOf(
                servicePort {
                    port = 6379
                    targetPort = IntOrString(6379)
                }
            )
        }
    }
}
