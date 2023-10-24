package eu.glasskube.operator.apps.vault.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.kubernetes.api.model.service
import eu.glasskube.kubernetes.api.model.servicePort
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.operator.apps.vault.Vault
import eu.glasskube.operator.apps.vault.VaultReconciler
import eu.glasskube.operator.apps.vault.resourceLabelSelector
import eu.glasskube.operator.apps.vault.resourceLabels
import eu.glasskube.operator.apps.vault.serviceName
import io.fabric8.kubernetes.api.model.Service
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(
    labelSelector = VaultReconciler.SELECTOR,
    resourceDiscriminator = VaultService.Discriminator::class
)
class VaultService : CRUDKubernetesDependentResource<Service, Vault>(Service::class.java) {

    internal class Discriminator :
        ResourceIDMatcherDiscriminator<Service, Vault>({ ResourceID(it.serviceName, it.namespace) })

    override fun desired(primary: Vault, context: Context<Vault>) = service {
        metadata {
            name(primary.serviceName)
            namespace(primary.namespace)
            labels(primary.resourceLabels)
        }
        spec {
            type = "ClusterIP"
            selector = primary.resourceLabelSelector
            ports = listOf(
                servicePort {
                    port = 8200
                    name = "https"
                }
            )
        }
    }
}
