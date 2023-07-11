package eu.glasskube.operator.apps.nextcloud.dependent

import eu.glasskube.kubernetes.api.model.extensions.ingress
import eu.glasskube.kubernetes.api.model.extensions.ingressBackend
import eu.glasskube.kubernetes.api.model.extensions.ingressPath
import eu.glasskube.kubernetes.api.model.extensions.ingressRule
import eu.glasskube.kubernetes.api.model.extensions.ingressRuleValue
import eu.glasskube.kubernetes.api.model.extensions.spec
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.operator.apps.nextcloud.Nextcloud
import eu.glasskube.operator.apps.nextcloud.NextcloudReconciler
import eu.glasskube.operator.apps.nextcloud.genericResourceName
import eu.glasskube.operator.apps.nextcloud.officeName
import eu.glasskube.operator.apps.nextcloud.officeTlsSecretName
import eu.glasskube.operator.apps.nextcloud.resourceLabels
import eu.glasskube.operator.apps.nextcloud.tlsSecretName
import eu.glasskube.operator.config.ConfigService
import eu.glasskube.operator.generic.dependent.DependentIngress
import io.fabric8.kubernetes.api.model.networking.v1.IngressTLS
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = NextcloudReconciler.SELECTOR)
class NextcloudIngress(configService: ConfigService) : DependentIngress<Nextcloud>(configService) {
    override fun desired(primary: Nextcloud, context: Context<Nextcloud>) = ingress {
        metadata {
            name = primary.genericResourceName
            namespace = primary.namespace
            labels = primary.resourceLabels
            annotations = primary.defaultAnnotations + mapOf(
                "nginx.ingress.kubernetes.io/proxy-body-size" to "10g"
            )
        }
        spec {
            ingressClassName = defaultIngressClassName
            rules = listOfNotNull(
                ingressRule {
                    host = primary.spec.host
                    http = ingressRuleValue(
                        ingressPath("/", "Prefix", ingressBackend(primary.genericResourceName, "http"))
                    )
                },
                primary.spec.apps.office?.let {
                    ingressRule {
                        host = it.host
                        http = ingressRuleValue(
                            ingressPath("/", "Prefix", ingressBackend(primary.officeName, "http"))
                        )
                    }
                }
            )
            tls = listOfNotNull(
                IngressTLS(listOf(primary.spec.host), primary.tlsSecretName),
                primary.spec.apps.office?.let {
                    IngressTLS(listOf(it.host), primary.officeTlsSecretName)
                }
            )
        }
    }
}
