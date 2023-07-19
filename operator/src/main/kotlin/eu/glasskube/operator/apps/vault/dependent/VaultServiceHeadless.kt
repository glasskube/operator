package eu.glasskube.operator.apps.vault.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.kubernetes.api.model.service
import eu.glasskube.kubernetes.api.model.servicePort
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.operator.apps.vault.Vault
import eu.glasskube.operator.apps.vault.VaultReconciler
import eu.glasskube.operator.apps.vault.headlessServiceName
import eu.glasskube.operator.apps.vault.resourceLabelSelector
import eu.glasskube.operator.apps.vault.resourceLabels
import io.fabric8.kubernetes.api.model.Service
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(
    labelSelector = VaultReconciler.SELECTOR,
    resourceDiscriminator = VaultServiceHeadless.Discriminator::class
)
class VaultServiceHeadless : CRUDKubernetesDependentResource<Service, Vault>(Service::class.java) {
    internal class Discriminator :
        ResourceIDMatcherDiscriminator<Service, Vault>({ ResourceID(it.headlessServiceName) })

    override fun desired(primary: Vault, context: Context<Vault>) = service {
        metadata {
            name = primary.headlessServiceName
            namespace = primary.namespace
            labels = primary.resourceLabels
        }
        spec {
            type = "ClusterIP"
            clusterIP = "None"
            publishNotReadyAddresses = true
            selector = primary.resourceLabelSelector
            ports = listOf(
                servicePort {
                    port = 8201
                    name = "https-internal"
                }
            )
        }
    }
}
