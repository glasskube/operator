package eu.glasskube.operator.apps.plane.dependent

import eu.glasskube.kubernetes.api.model.intOrString
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.kubernetes.api.model.service
import eu.glasskube.kubernetes.api.model.servicePort
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.operator.apps.plane.Plane
import eu.glasskube.operator.apps.plane.spaceResourceLabelSelector
import eu.glasskube.operator.apps.plane.spaceResourceLabels
import eu.glasskube.operator.apps.plane.spaceResourceName
import io.fabric8.kubernetes.api.model.Service
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(resourceDiscriminator = PlaneSpaceService.Discriminator::class)
class PlaneSpaceService : CRUDKubernetesDependentResource<Service, Plane>(Service::class.java) {

    internal class Discriminator :
        ResourceIDMatcherDiscriminator<Service, Plane>({ ResourceID(it.spaceResourceName, it.namespace) })

    override fun desired(primary: Plane, context: Context<Plane>) = service {
        metadata {
            name(primary.spaceResourceName)
            namespace(primary.namespace)
            labels(primary.spaceResourceLabels)
        }
        spec {
            type = "ClusterIP"
            selector = primary.spaceResourceLabelSelector
            ports = listOf(
                servicePort {
                    port = 3000
                    targetPort = intOrString(3000)
                }
            )
        }
    }
}
