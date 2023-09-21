package eu.glasskube.operator.apps.gitea.dependent

import eu.glasskube.kubernetes.api.model.intOrString
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.service
import eu.glasskube.kubernetes.api.model.servicePort
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.operator.apps.gitea.Gitea
import eu.glasskube.operator.apps.gitea.GiteaReconciler
import eu.glasskube.operator.apps.gitea.resourceLabelSelector
import eu.glasskube.operator.apps.gitea.resourceLabels
import eu.glasskube.operator.apps.gitea.sshServiceName
import eu.glasskube.operator.config.ConfigService
import io.fabric8.kubernetes.api.model.Service
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.api.reconciler.dependent.DependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.dependent.workflow.Condition
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(
    labelSelector = GiteaReconciler.SELECTOR,
    resourceDiscriminator = GiteaSSHService.Discriminator::class
)
class GiteaSSHService(private val configService: ConfigService) :
    CRUDKubernetesDependentResource<Service, Gitea>(Service::class.java) {
    internal class Discriminator : ResourceIDMatcherDiscriminator<Service, Gitea>({ ResourceID(it.sshServiceName) })

    class ReconcileCondition : Condition<Service, Gitea> {
        override fun isMet(
            dependentResource: DependentResource<Service, Gitea>,
            primary: Gitea,
            context: Context<Gitea>
        ) = primary.spec.sshEnabled
    }

    override fun desired(primary: Gitea, context: Context<Gitea>) = service {
        metadata {
            name = primary.sshServiceName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
            annotations = configService.getCommonLoadBalancerAnnotations(primary)
        }
        spec {
            type = "LoadBalancer"
            selector = primary.resourceLabelSelector
            ports = listOf(
                servicePort {
                    port = 22
                    name = "ssh"
                    targetPort = intOrString(22)
                }
            )
        }
    }
}
