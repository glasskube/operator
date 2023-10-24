package eu.glasskube.operator.apps.nextcloud.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.kubernetes.api.model.service
import eu.glasskube.kubernetes.api.model.servicePort
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.operator.apps.nextcloud.Nextcloud
import eu.glasskube.operator.apps.nextcloud.NextcloudReconciler
import eu.glasskube.operator.apps.nextcloud.genericResourceName
import eu.glasskube.operator.apps.nextcloud.resourceLabelSelector
import eu.glasskube.operator.apps.nextcloud.resourceLabels
import io.fabric8.kubernetes.api.model.Service
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

// TODO: Add support for FastCGI without nginx proxy if Ingress controller supports it
@KubernetesDependent(
    labelSelector = NextcloudReconciler.SELECTOR,
    resourceDiscriminator = NextcloudService.Discriminator::class
)
class NextcloudService : CRUDKubernetesDependentResource<Service, Nextcloud>(Service::class.java) {

    internal class Discriminator : ResourceIDMatcherDiscriminator<Service, Nextcloud>({
        ResourceID(it.genericResourceName, it.namespace)
    })

    override fun desired(primary: Nextcloud, context: Context<Nextcloud>) = service {
        metadata {
            name(primary.genericResourceName)
            namespace(primary.namespace)
            labels(primary.resourceLabels)
        }
        spec {
            type = "ClusterIP"
            selector = primary.resourceLabelSelector
            ports = listOf(
                servicePort {
                    port = 80
                    name = "http"
                }
            )
        }
    }
}
