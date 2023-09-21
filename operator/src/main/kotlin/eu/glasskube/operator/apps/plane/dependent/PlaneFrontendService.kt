package eu.glasskube.operator.apps.plane.dependent

import eu.glasskube.kubernetes.api.model.intOrString
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.kubernetes.api.model.service
import eu.glasskube.kubernetes.api.model.servicePort
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.operator.apps.plane.Plane
import eu.glasskube.operator.apps.plane.frontendResourceLabelSelector
import eu.glasskube.operator.apps.plane.frontendResourceLabels
import eu.glasskube.operator.apps.plane.frontendResourceName
import io.fabric8.kubernetes.api.model.Service
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(resourceDiscriminator = PlaneFrontendService.Discriminator::class)
class PlaneFrontendService : CRUDKubernetesDependentResource<Service, Plane>(Service::class.java) {
    internal class Discriminator :
        ResourceIDMatcherDiscriminator<Service, Plane>({ ResourceID(it.frontendResourceName) })

    override fun desired(primary: Plane, context: Context<Plane>) = service {
        metadata {
            name = primary.frontendResourceName
            namespace = primary.namespace
            labels = primary.frontendResourceLabels
        }
        spec {
            type = "ClusterIP"
            selector = primary.frontendResourceLabelSelector
            ports = listOf(
                servicePort {
                    port = 3000
                    targetPort = intOrString(3000)
                }
            )
        }
    }
}
