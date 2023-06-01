package eu.glasskube.operator.apps.gitlab.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.service
import eu.glasskube.kubernetes.api.model.servicePort
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.operator.apps.gitlab.Gitlab
import eu.glasskube.operator.apps.gitlab.GitlabReconciler
import eu.glasskube.operator.apps.gitlab.resourceLabelSelector
import eu.glasskube.operator.apps.gitlab.resourceLabels
import eu.glasskube.operator.apps.gitlab.sshServiceName
import eu.glasskube.operator.config.ConfigService
import eu.glasskube.operator.generic.dependent.UpdatableAnnotationsCRUDKubernetesDependentResource
import io.fabric8.kubernetes.api.model.Service
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.api.reconciler.dependent.DependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.dependent.workflow.Condition
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(
    labelSelector = GitlabReconciler.SELECTOR,
    resourceDiscriminator = GitlabSSHService.Discriminator::class
)
class GitlabSSHService(private val configService: ConfigService) :
    UpdatableAnnotationsCRUDKubernetesDependentResource<Service, Gitlab>(Service::class.java) {

    internal class Discriminator : ResourceIDMatcherDiscriminator<Service, Gitlab>({ ResourceID(it.sshServiceName) })

    class ReconcileCondition : Condition<Service, Gitlab> {
        override fun isMet(
            dependentResource: DependentResource<Service, Gitlab>,
            primary: Gitlab,
            context: Context<Gitlab>
        ) = primary.spec.sshEnabled
    }

    override fun desired(primary: Gitlab, context: Context<Gitlab>) = service {
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
                }
            )
        }
    }
}
