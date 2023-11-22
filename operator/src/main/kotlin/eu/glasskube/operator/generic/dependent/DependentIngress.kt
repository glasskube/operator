package eu.glasskube.operator.generic.dependent

import eu.glasskube.operator.config.CloudProvider
import eu.glasskube.operator.config.ConfigService
import io.fabric8.kubernetes.api.model.GenericKubernetesResource
import io.fabric8.kubernetes.api.model.HasMetadata
import io.fabric8.kubernetes.api.model.networking.v1.Ingress
import io.fabric8.kubernetes.client.dsl.base.ResourceDefinitionContext
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource

abstract class DependentIngress<P : HasMetadata>(private val configService: ConfigService) :
    CRUDKubernetesDependentResource<Ingress, P>(Ingress::class.java) {

    protected val defaultIngressClassName: String?
        get() = when (configService.cloudProvider) {
            CloudProvider.aws -> "alb"
            else -> configService.ingressClassName
        }

    protected fun getDefaultAnnotations(primary: P, context: Context<P>): Map<String, String> =
        configService.getCommonIngressAnnotations(primary) +
            when (configService.cloudProvider) {
                CloudProvider.aws -> awsDefaultAnnotations
                CloudProvider.gardener -> gardenerDefaultAnnotations
                else -> getCertManagerDefaultAnnotations(context) + ingressNginxDefaultAnnotations
            }

    private val awsDefaultAnnotations
        get() = mapOf(
            "alb.ingress.kubernetes.io/listen-ports" to "[{\"HTTP\": 80}, {\"HTTPS\": 443}]",
            "alb.ingress.kubernetes.io/scheme" to "internet-facing",
            "alb.ingress.kubernetes.io/target-type" to "ip",
            "alb.ingress.kubernetes.io/ssl-redirect" to "443",
            "alb.ingress.kubernetes.io/group.name" to "glasskube"
        )

    private val gardenerDefaultAnnotations
        get() = mapOf(
            "dns.gardener.cloud/class" to "garden",
            "dns.gardener.cloud/dnsnames" to "*",
            "dns.gardener.cloud/ttl" to "600"
        )

    private fun getCertManagerDefaultAnnotations(context: Context<P>) =
        when (val clusterIssuer = getDefaultClusterIssuer(context)) {
            null -> emptyMap()
            else -> mapOf("cert-manager.io/cluster-issuer" to clusterIssuer.metadata.name)
        }

    private val ingressNginxDefaultAnnotations
        get() = mapOf(
            "nginx.ingress.kubernetes.io/proxy-body-size" to "256m",
            "nginx.ingress.kubernetes.io/proxy-next-upstream-tries" to "10"
        )

    private fun getDefaultClusterIssuer(context: Context<P>): GenericKubernetesResource? {
        val clusterIssuerContext = ResourceDefinitionContext.Builder()
            .withKind("ClusterIssuer")
            .withGroup("cert-manager.io")
            .withVersion("v1")
            .build()
        return context.client.genericKubernetesResources(clusterIssuerContext).list().items.firstOrNull()
    }
}
