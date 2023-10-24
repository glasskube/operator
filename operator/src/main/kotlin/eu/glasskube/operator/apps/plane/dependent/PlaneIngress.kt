package eu.glasskube.operator.apps.plane.dependent

import eu.glasskube.kubernetes.api.model.extensions.ingress
import eu.glasskube.kubernetes.api.model.extensions.ingressBackend
import eu.glasskube.kubernetes.api.model.extensions.ingressPath
import eu.glasskube.kubernetes.api.model.extensions.ingressRule
import eu.glasskube.kubernetes.api.model.extensions.ingressRuleValue
import eu.glasskube.kubernetes.api.model.extensions.spec
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.operator.apps.plane.Plane
import eu.glasskube.operator.apps.plane.apiResourceName
import eu.glasskube.operator.apps.plane.frontendResourceName
import eu.glasskube.operator.apps.plane.genericResourceLabels
import eu.glasskube.operator.apps.plane.genericResourceName
import eu.glasskube.operator.apps.plane.spaceResourceName
import eu.glasskube.operator.apps.plane.tlsSecretName
import eu.glasskube.operator.config.ConfigService
import eu.glasskube.operator.generic.dependent.DependentIngress
import io.fabric8.kubernetes.api.model.networking.v1.IngressTLS
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent
class PlaneIngress(configService: ConfigService) : DependentIngress<Plane>(configService) {
    override fun desired(primary: Plane, context: Context<Plane>) = ingress {
        metadata {
            name(primary.genericResourceName)
            namespace(primary.namespace)
            labels(primary.genericResourceLabels)
            annotations(getDefaultAnnotations(primary, context))
        }
        spec {
            ingressClassName = defaultIngressClassName
            rules = listOf(
                ingressRule {
                    host = primary.spec.host
                    http = ingressRuleValue(
                        ingressPath("/api/", "Prefix", ingressBackend(primary.apiResourceName, 8000)),
                        ingressPath("/spaces/", "Prefix", ingressBackend(primary.spaceResourceName, 3000)),
                        ingressPath("/", "Prefix", ingressBackend(primary.frontendResourceName, 3000))
                    )
                }
            )
            tls = listOf(
                IngressTLS(listOf(primary.spec.host), primary.tlsSecretName)
            )
        }
    }
}
