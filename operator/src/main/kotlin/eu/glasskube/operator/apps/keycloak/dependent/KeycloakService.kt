package eu.glasskube.operator.apps.keycloak.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.kubernetes.api.model.service
import eu.glasskube.kubernetes.api.model.servicePort
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.operator.apps.keycloak.Keycloak
import eu.glasskube.operator.apps.keycloak.KeycloakReconciler
import eu.glasskube.operator.apps.keycloak.genericResourceName
import eu.glasskube.operator.apps.keycloak.resourceLabelSelector
import eu.glasskube.operator.apps.keycloak.resourceLabels
import io.fabric8.kubernetes.api.model.Service
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(
    labelSelector = KeycloakReconciler.SELECTOR,
    resourceDiscriminator = KeycloakService.Discriminator::class
)
class KeycloakService : CRUDKubernetesDependentResource<Service, Keycloak>(Service::class.java) {
    internal class Discriminator : ResourceIDMatcherDiscriminator<Service, Keycloak>({
        ResourceID(it.genericResourceName, it.namespace)
    })

    override fun desired(primary: Keycloak, context: Context<Keycloak>) = service {
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
                    port = 8080
                    name = "http"
                }
            )
        }
    }
}
