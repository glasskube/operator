package eu.glasskube.operator.httpecho.dependent

import eu.glasskube.kubernetes.api.model.extensions.*
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.httpecho.HttpEcho
import eu.glasskube.operator.httpecho.HttpEchoReconciler
import eu.glasskube.operator.httpecho.resourceLabels
import io.fabric8.kubernetes.api.model.networking.v1.Ingress
import io.fabric8.kubernetes.api.model.networking.v1.IngressRule
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = HttpEchoReconciler.SELECTOR)
class HttpEchoIngress : CRUDKubernetesDependentResource<Ingress, HttpEcho>(Ingress::class.java) {
    override fun desired(primary: HttpEcho, context: Context<HttpEcho>): Ingress = ingress {
        metadata {
            name = primary.metadata.name
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        spec {
            rules = listOf(
                IngressRule(
                    primary.spec.host,
                    ingressRuleValue(
                        ingressPath(
                            path = "/",
                            pathType = "Prefix",
                            backend = ingressBackend(
                                serviceName = primary.metadata.name,
                                servicePort = "http"
                            )
                        )
                    )
                )
            )
        }
    }
}
