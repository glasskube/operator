package eu.glasskube.operator.gitea.dependent

import eu.glasskube.kubernetes.api.model.apps.deployment
import eu.glasskube.kubernetes.api.model.apps.selector
import eu.glasskube.kubernetes.api.model.apps.spec
import eu.glasskube.kubernetes.api.model.apps.template
import eu.glasskube.kubernetes.api.model.container
import eu.glasskube.kubernetes.api.model.containerPort
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.operator.gitea.Gitea
import eu.glasskube.operator.gitea.GiteaReconciler
import eu.glasskube.operator.gitea.redisLabelSelector
import eu.glasskube.operator.gitea.redisLabels
import eu.glasskube.operator.gitea.redisName
import io.fabric8.kubernetes.api.model.Quantity
import io.fabric8.kubernetes.api.model.ResourceRequirements
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(
    labelSelector = GiteaReconciler.REDIS_SELECTOR,
    resourceDiscriminator = GiteaRedisDeployment.Discriminator::class
)
class GiteaRedisDeployment : CRUDKubernetesDependentResource<Deployment, Gitea>(Deployment::class.java) {
    internal class Discriminator : ResourceIDMatcherDiscriminator<Deployment, Gitea>({ ResourceID(it.redisName, it.metadata.namespace) })

    override fun desired(primary: Gitea, context: Context<Gitea>) = deployment {
        metadata {
            name = primary.redisName
            namespace = primary.metadata.namespace
            labels = primary.redisLabels
        }
        spec {
            selector {
                matchLabels = primary.redisLabelSelector
            }
            template {
                metadata {
                    labels = primary.redisLabels
                }
                spec {
                    containers = listOf(
                        container {
                            name = "redis"
                            image = "redis:${Gitea.REDIS_VERSION}-alpine"
                            resources = ResourceRequirements(
                                mapOf("memory" to Quantity("128", "Mi")),
                                mapOf("memory" to Quantity("10", "Mi"))
                            )
                            ports = listOf(
                                containerPort { containerPort = 6379 }
                            )
                        }
                    )
                }
            }
        }
    }
}
