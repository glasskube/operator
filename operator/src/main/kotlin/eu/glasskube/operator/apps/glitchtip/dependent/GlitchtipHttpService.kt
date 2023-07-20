package eu.glasskube.operator.apps.glitchtip.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.service
import eu.glasskube.kubernetes.api.model.servicePort
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.operator.apps.glitchtip.Glitchtip
import eu.glasskube.operator.apps.glitchtip.GlitchtipReconciler
import eu.glasskube.operator.apps.glitchtip.httpServiceName
import eu.glasskube.operator.apps.glitchtip.resourceLabelSelector
import eu.glasskube.operator.apps.glitchtip.resourceLabels
import io.fabric8.kubernetes.api.model.Service
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(
    labelSelector = GlitchtipReconciler.SELECTOR,
    resourceDiscriminator = GlitchtipHttpService.Discriminator::class
)
class GlitchtipHttpService : CRUDKubernetesDependentResource<Service, Glitchtip>(Service::class.java) {
    internal class Discriminator : ResourceIDMatcherDiscriminator<Service, Glitchtip>({ ResourceID(it.httpServiceName) })

    override fun desired(primary: Glitchtip, context: Context<Glitchtip>) = service {
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
                    port = 8080
                    name = "http"
                },
                servicePort {
                    port = 9191
                    name = "glitchtip-exp"
                }
            )
        }
    }
}
