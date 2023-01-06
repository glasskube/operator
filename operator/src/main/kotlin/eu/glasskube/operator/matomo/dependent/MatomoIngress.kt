package eu.glasskube.operator.matomo.dependent

import eu.glasskube.kubernetes.api.model.extensions.ingress
import eu.glasskube.kubernetes.api.model.extensions.ingressBackend
import eu.glasskube.kubernetes.api.model.extensions.ingressPath
import eu.glasskube.kubernetes.api.model.extensions.ingressRuleValue
import eu.glasskube.kubernetes.api.model.extensions.spec
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.config.CloudProvider
import eu.glasskube.operator.getCloudProvider
import eu.glasskube.operator.matomo.Matomo
import eu.glasskube.operator.matomo.MatomoReconciler
import eu.glasskube.operator.matomo.ingressName
import eu.glasskube.operator.matomo.ingressTlsCertName
import eu.glasskube.operator.matomo.resourceLabels
import eu.glasskube.operator.matomo.serviceName
import io.fabric8.kubernetes.api.model.networking.v1.Ingress
import io.fabric8.kubernetes.api.model.networking.v1.IngressRule
import io.fabric8.kubernetes.api.model.networking.v1.IngressTLS
import io.fabric8.kubernetes.client.dsl.base.ResourceDefinitionContext
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = MatomoReconciler.SELECTOR)
class MatomoIngress : CRUDKubernetesDependentResource<Ingress, Matomo>(Ingress::class.java) {
    private fun configureIngressClassName(): String {
        return when (getCloudProvider(client)) {
            CloudProvider.aws -> "alb"
            else -> "nginx"
        }
    }

    private fun configureAnnotations(): Map<String, String> {
        return when (getCloudProvider(client)) {
            CloudProvider.aws -> mapOf(
                "alb.ingress.kubernetes.io/listen-ports" to "[{\"HTTP\": 80}, {\"HTTPS\": 443}]",
                "alb.ingress.kubernetes.io/scheme" to "internet-facing",
                "alb.ingress.kubernetes.io/target-type" to "ip",
                "alb.ingress.kubernetes.io/ssl-redirect" to "443",
                "alb.ingress.kubernetes.io/group.name" to "glasskube"
            )

            CloudProvider.generic -> setClusterIssuerIfPresent()

            else -> emptyMap()
        }
    }

    private fun setClusterIssuerIfPresent(): Map<String, String> {
        val clusterIssuerContext = ResourceDefinitionContext.Builder()
            .withKind("ClusterIssuer")
            .withGroup("cert-manager.io")
            .withVersion("v1")
            .build()

        val items = client.genericKubernetesResources(clusterIssuerContext).list().items

        if (items.isNotEmpty()) {
            return mapOf(
                "cert-manager.io/cluster-issuer" to items[0].metadata.name
            )
        }

        return emptyMap()
    }

    override fun desired(primary: Matomo, context: Context<Matomo>) = ingress {
        metadata {
            name = primary.ingressName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
            annotations = configureAnnotations()
        }
        spec {
            ingressClassName = configureIngressClassName()
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
