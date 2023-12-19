package eu.glasskube.operator.apps.plane.dependent

import eu.glasskube.kubernetes.api.model.intOrString
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.kubernetes.api.model.service
import eu.glasskube.kubernetes.api.model.servicePort
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.operator.apps.plane.Plane
import eu.glasskube.operator.apps.plane.apiResourceLabelSelector
import eu.glasskube.operator.apps.plane.apiResourceLabels
import eu.glasskube.operator.apps.plane.apiResourceName
import io.fabric8.kubernetes.api.model.Service
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(resourceDiscriminator = PlaneApiService.Discriminator::class)
class PlaneApiService : CRUDKubernetesDependentResource<Service, Plane>(Service::class.java) {

    internal class Discriminator :
        ResourceIDMatcherDiscriminator<Service, Plane>({ ResourceID(it.apiResourceName, it.namespace) })

    override fun desired(primary: Plane, context: Context<Plane>) = service {
        metadata {
            name(primary.apiResourceName)
            namespace(primary.namespace)
            labels(primary.apiResourceLabels)
        }
        spec {
            type = "ClusterIP"
            selector = primary.apiResourceLabelSelector
            ports = listOf(
                servicePort {
                    port = 8000
                    targetPort = intOrString(8000)
                }
            )
        }
    }
}
