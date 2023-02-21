package eu.glasskube.operator.generic.dependant

import eu.glasskube.operator.config.CloudProvider
import eu.glasskube.operator.config.ConfigService
import io.fabric8.kubernetes.api.model.GenericKubernetesResource
import io.fabric8.kubernetes.api.model.HasMetadata
import io.fabric8.kubernetes.api.model.networking.v1.Ingress
import io.fabric8.kubernetes.client.dsl.base.ResourceDefinitionContext
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource

abstract class DependentIngress<T : HasMetadata>(private val configService: ConfigService) :
    CRUDKubernetesDependentResource<Ingress, T>(Ingress::class.java) {

    protected val defaultIngressClassName: String?
        get() = when (configService.cloudProvider) {
            CloudProvider.aws -> "alb"
            else -> configService.ingressClassName
        }

    protected val defaultAnnotations: Map<String, String>
        get() = when (configService.cloudProvider) {
            CloudProvider.aws -> awsDefaultAnnotations
            else -> certManagerDefaultAnnotations
        }

    private val awsDefaultAnnotations
        get() = mapOf(
            "alb.ingress.kubernetes.io/listen-ports" to "[{\"HTTP\": 80}, {\"HTTPS\": 443}]",
            "alb.ingress.kubernetes.io/scheme" to "internet-facing",
            "alb.ingress.kubernetes.io/target-type" to "ip",
            "alb.ingress.kubernetes.io/ssl-redirect" to "443",
            "alb.ingress.kubernetes.io/group.name" to "glasskube"
        )

    private val certManagerDefaultAnnotations
        get() = when (val clusterIssuer = defaultClusterIssuer) {
            null -> emptyMap()
            else -> mapOf("cert-manager.io/cluster-issuer" to clusterIssuer.metadata.name)
        }

    private val defaultClusterIssuer: GenericKubernetesResource?
        get() {
            val clusterIssuerContext = ResourceDefinitionContext.Builder()
                .withKind("ClusterIssuer")
                .withGroup("cert-manager.io")
                .withVersion("v1")
                .build()
            return client.genericKubernetesResources(clusterIssuerContext).list().items.firstOrNull()
        }
}
