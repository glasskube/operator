package eu.glasskube.operator.apps.metabase.dependent

import eu.glasskube.kubernetes.api.model.extensions.ingress
import eu.glasskube.kubernetes.api.model.extensions.ingressBackend
import eu.glasskube.kubernetes.api.model.extensions.ingressPath
import eu.glasskube.kubernetes.api.model.extensions.ingressRule
import eu.glasskube.kubernetes.api.model.extensions.ingressRuleValue
import eu.glasskube.kubernetes.api.model.extensions.spec
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.apps.metabase.Metabase
import eu.glasskube.operator.apps.metabase.MetabaseReconciler
import eu.glasskube.operator.apps.metabase.httpServiceName
import eu.glasskube.operator.apps.metabase.ingressName
import eu.glasskube.operator.apps.metabase.ingressTlsCertName
import eu.glasskube.operator.apps.metabase.resourceLabels
import eu.glasskube.operator.config.ConfigService
import eu.glasskube.operator.generic.dependent.DependentIngress
import io.fabric8.kubernetes.api.model.networking.v1.IngressTLS
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = MetabaseReconciler.SELECTOR)
class MetabaseIngress(configService: ConfigService) : DependentIngress<Metabase>(configService) {
    override fun desired(primary: Metabase, context: Context<Metabase>) = ingress {
        metadata {
            name(primary.ingressName)
            namespace(primary.metadata.namespace)
            labels(primary.resourceLabels)
            annotations(
                getDefaultAnnotations(primary, context) +
                    ("nginx.ingress.kubernetes.io/proxy-body-size" to "256m")
            )
        }
        spec {
            ingressClassName = defaultIngressClassName
            rules = listOf(
                ingressRule {
                    host = primary.spec.host
                    http = ingressRuleValue(
                        ingressPath(
                            path = "/",
                            pathType = "Prefix",
                            backend = ingressBackend(primary.httpServiceName, "http")
                        )
                    )
                }
            )
            tls = listOf(
                IngressTLS(listOf(primary.spec.host), primary.ingressTlsCertName)
            )
        }
    }
}
