package eu.glasskube.operator.apps.keycloak.dependent

import eu.glasskube.kubernetes.api.model.extensions.ingress
import eu.glasskube.kubernetes.api.model.extensions.ingressBackend
import eu.glasskube.kubernetes.api.model.extensions.ingressPath
import eu.glasskube.kubernetes.api.model.extensions.ingressRule
import eu.glasskube.kubernetes.api.model.extensions.ingressRuleValue
import eu.glasskube.kubernetes.api.model.extensions.spec
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.operator.apps.keycloak.Keycloak
import eu.glasskube.operator.apps.keycloak.KeycloakReconciler
import eu.glasskube.operator.apps.keycloak.genericResourceName
import eu.glasskube.operator.apps.keycloak.ingressTlsCertName
import eu.glasskube.operator.apps.keycloak.resourceLabels
import eu.glasskube.operator.config.ConfigService
import eu.glasskube.operator.generic.dependent.DependentIngress
import io.fabric8.kubernetes.api.model.networking.v1.IngressTLS
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = KeycloakReconciler.SELECTOR)
class KeycloakIngress(configService: ConfigService) : DependentIngress<Keycloak>(configService) {
    override fun desired(primary: Keycloak, context: Context<Keycloak>) = ingress {
        metadata {
            name(primary.genericResourceName)
            namespace(primary.namespace)
            labels(primary.resourceLabels)
            annotations(
                getDefaultAnnotations(primary, context) +
                    ("nginx.ingress.kubernetes.io/proxy-buffer-size" to "128k")
            )
        }
        spec {
            ingressClassName = defaultIngressClassName
            rules = listOf(
                ingressRule {
                    host = primary.spec.host
                    http = ingressRuleValue(*primary.ingressPaths.toTypedArray())
                }
            )
            tls = listOf(
                IngressTLS(listOf(primary.spec.host), primary.ingressTlsCertName)
            )
        }
    }

    private val Keycloak.ingressPaths
        get() = exposedPrefixPaths.map { mapToIngressPath(it) } +
            exposedExactPaths.map { mapToIngressPath(it, "Exact") }

    /**
     * Exposed paths as recommended in [Using a reverse proxy](https://www.keycloak.org/server/reverseproxy#_exposed_path_recommendations)
     */
    private val Keycloak.exposedPrefixPaths
        get() = listOf("/js/", "/realms/", "/resources/") +
            if (spec.management.enabled) listOf("/admin/", "/welcome-content/") else emptyList()

    private val Keycloak.exposedExactPaths
        get() = listOf("/robots.txt") +
            if (spec.management.enabled) listOf("/") else emptyList()

    private fun Keycloak.mapToIngressPath(path: String, pathType: String = "Prefix") =
        ingressPath(path, pathType, ingressBackend(genericResourceName, "http"))
}
