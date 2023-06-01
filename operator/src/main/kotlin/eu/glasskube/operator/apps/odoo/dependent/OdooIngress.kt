package eu.glasskube.operator.apps.odoo.dependent

import eu.glasskube.kubernetes.api.model.extensions.ingress
import eu.glasskube.kubernetes.api.model.extensions.ingressBackend
import eu.glasskube.kubernetes.api.model.extensions.ingressPath
import eu.glasskube.kubernetes.api.model.extensions.ingressRuleValue
import eu.glasskube.kubernetes.api.model.extensions.spec
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.apps.odoo.Odoo
import eu.glasskube.operator.apps.odoo.OdooReconciler
import eu.glasskube.operator.apps.odoo.ingressName
import eu.glasskube.operator.apps.odoo.ingressTlsCertName
import eu.glasskube.operator.apps.odoo.resourceLabels
import eu.glasskube.operator.apps.odoo.serviceName
import eu.glasskube.operator.config.ConfigService
import eu.glasskube.operator.generic.dependent.DependentIngress
import io.fabric8.kubernetes.api.model.networking.v1.IngressRule
import io.fabric8.kubernetes.api.model.networking.v1.IngressTLS
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = OdooReconciler.SELECTOR)
class OdooIngress(configService: ConfigService) : DependentIngress<Odoo>(configService) {
    override fun desired(primary: Odoo, context: Context<Odoo>) = ingress {
        metadata {
            name = primary.ingressName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
            annotations = primary.defaultAnnotations
        }
        spec {
            ingressClassName = defaultIngressClassName
            rules = listOf(
                IngressRule(
                    primary.spec.host,
                    ingressRuleValue(
                        ingressPath(
                            path = "/",
                            pathType = "Prefix",
                            backend = ingressBackend(
                                serviceName = primary.serviceName,
                                servicePort = "http"
                            )
                        )
                    )
                )
            )
            tls = listOf(
                IngressTLS(listOf(primary.spec.host), primary.ingressTlsCertName)
            )
        }
    }
}
