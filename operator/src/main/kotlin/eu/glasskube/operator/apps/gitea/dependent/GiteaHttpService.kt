package eu.glasskube.operator.apps.gitea.dependent

import eu.glasskube.kubernetes.api.model.intOrString
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.service
import eu.glasskube.kubernetes.api.model.servicePort
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.operator.apps.gitea.Gitea
import eu.glasskube.operator.apps.gitea.GiteaReconciler
import eu.glasskube.operator.apps.gitea.httpServiceName
import eu.glasskube.operator.apps.gitea.resourceLabelSelector
import eu.glasskube.operator.apps.gitea.resourceLabels
import io.fabric8.kubernetes.api.model.Service
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(
    labelSelector = GiteaReconciler.SELECTOR,
    resourceDiscriminator = GiteaHttpService.Discriminator::class
)
class GiteaHttpService : CRUDKubernetesDependentResource<Service, Gitea>(Service::class.java) {
    internal class Discriminator : ResourceIDMatcherDiscriminator<Service, Gitea>({ ResourceID(it.httpServiceName) })

    override fun desired(primary: Gitea, context: Context<Gitea>) = service {
        metadata {
            name = primary.httpServiceName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        spec {
            type = "ClusterIP"
            selector = primary.resourceLabelSelector
            ports = listOf(
                servicePort {
                    port = 3000
                    name = "http"
                    targetPort = intOrString(3000)
                }
            )
        }
    }
}
