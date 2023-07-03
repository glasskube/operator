package eu.glasskube.operator.apps.nextcloud.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.kubernetes.api.model.service
import eu.glasskube.kubernetes.api.model.servicePort
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.operator.apps.nextcloud.Nextcloud
import eu.glasskube.operator.apps.nextcloud.NextcloudReconciler
import eu.glasskube.operator.apps.nextcloud.officeName
import eu.glasskube.operator.apps.nextcloud.officeResourceLabelSelector
import eu.glasskube.operator.apps.nextcloud.officeResourceLabels
import io.fabric8.kubernetes.api.model.Service
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(
    labelSelector = NextcloudReconciler.OFFICE_SELECTOR,
    resourceDiscriminator = NextcloudOfficeService.Discriminator::class
)
class NextcloudOfficeService : CRUDKubernetesDependentResource<Service, Nextcloud>(Service::class.java) {

    class ReconcilePrecondition : IsOfficeEnabledPrecondition<Service>()

    internal class Discriminator : ResourceIDMatcherDiscriminator<Service, Nextcloud>({
        ResourceID(it.officeName, it.namespace)
    })

    override fun desired(primary: Nextcloud, context: Context<Nextcloud>) = service {
        metadata {
            name = primary.officeName
            namespace = primary.namespace
            labels = primary.officeResourceLabels
        }
        spec {
            type = "ClusterIP"
            selector = primary.officeResourceLabelSelector
            ports = listOf(
                servicePort {
                    port = 9980
                    name = "http"
                }
            )
        }
    }
}
