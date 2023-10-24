package eu.glasskube.operator.apps.vault.dependent

import eu.glasskube.kubernetes.api.model.extensions.ingress
import eu.glasskube.kubernetes.api.model.extensions.ingressBackend
import eu.glasskube.kubernetes.api.model.extensions.ingressPath
import eu.glasskube.kubernetes.api.model.extensions.ingressRule
import eu.glasskube.kubernetes.api.model.extensions.ingressRuleValue
import eu.glasskube.kubernetes.api.model.extensions.spec
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.operator.apps.vault.Vault
import eu.glasskube.operator.apps.vault.VaultReconciler
import eu.glasskube.operator.apps.vault.genericResourceName
import eu.glasskube.operator.apps.vault.resourceLabels
import eu.glasskube.operator.apps.vault.serviceName
import eu.glasskube.operator.apps.vault.tlsSecretName
import eu.glasskube.operator.config.ConfigService
import eu.glasskube.operator.generic.dependent.DependentIngress
import io.fabric8.kubernetes.api.model.networking.v1.IngressTLS
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = VaultReconciler.SELECTOR)
class VaultIngress(configService: ConfigService) : DependentIngress<Vault>(configService) {
    override fun desired(primary: Vault, context: Context<Vault>) = ingress {
        metadata {
            name(primary.genericResourceName)
            namespace(primary.namespace)
            labels(primary.resourceLabels)
            annotations(
                getDefaultAnnotations(primary, context) +
                    ("nginx.ingress.kubernetes.io/ssl-passthrough" to "true")
            )
        }
        spec {
            ingressClassName = defaultIngressClassName
            rules = listOf(
                ingressRule {
                    host = primary.spec.host
                    http = ingressRuleValue(
                        ingressPath("/", "Prefix", ingressBackend(primary.serviceName, 8200))
                    )
                }
            )
            tls = listOf(
                IngressTLS(listOf(primary.spec.host), primary.tlsSecretName)
            )
        }
    }
}
