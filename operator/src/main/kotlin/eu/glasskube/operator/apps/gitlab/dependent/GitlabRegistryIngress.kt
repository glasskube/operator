package eu.glasskube.operator.apps.gitlab.dependent

import eu.glasskube.kubernetes.api.model.extensions.ingress
import eu.glasskube.kubernetes.api.model.extensions.ingressBackend
import eu.glasskube.kubernetes.api.model.extensions.ingressPath
import eu.glasskube.kubernetes.api.model.extensions.ingressRule
import eu.glasskube.kubernetes.api.model.extensions.ingressRuleValue
import eu.glasskube.kubernetes.api.model.extensions.spec
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.operator.apps.gitlab.Gitlab
import eu.glasskube.operator.apps.gitlab.GitlabReconciler
import eu.glasskube.operator.apps.gitlab.genericRegistryResourceName
import eu.glasskube.operator.apps.gitlab.resourceLabels
import eu.glasskube.operator.apps.gitlab.serviceName
import eu.glasskube.operator.config.ConfigService
import eu.glasskube.operator.generic.dependent.DependentIngress
import io.fabric8.kubernetes.api.model.networking.v1.Ingress
import io.fabric8.kubernetes.api.model.networking.v1.IngressTLS
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(
    labelSelector = GitlabReconciler.SELECTOR,
    resourceDiscriminator = GitlabRegistryIngress.Discriminator::class
)
class GitlabRegistryIngress(configService: ConfigService) : DependentIngress<Gitlab>(configService) {

    class ReconcilePrecondition : GitlabRegistryEnabledPrecondition<Ingress>()

    internal class Discriminator :
        ResourceIDMatcherDiscriminator<Ingress, Gitlab>({ ResourceID(it.genericRegistryResourceName, it.namespace) })

    override fun desired(primary: Gitlab, context: Context<Gitlab>) = ingress {
        metadata {
            name(primary.genericRegistryResourceName)
            namespace(primary.metadata.namespace)
            labels(primary.resourceLabels)
            annotations(
                getDefaultAnnotations(primary, context) +
                    ("nginx.ingress.kubernetes.io/proxy-body-size" to "256m") +
                    ("nginx.ingress.kubernetes.io/ssl-passthrough" to "true")
            )
        }
        spec {
            ingressClassName = defaultIngressClassName
            rules = listOf(
                ingressRule {
                    host = primary.spec.registry!!.host
                    http = ingressRuleValue(
                        ingressPath(
                            path = "/",
                            pathType = "Prefix",
                            backend = ingressBackend(primary.serviceName, "registry-ssl")
                        )
                    )
                }
            )
            tls = listOf(
                IngressTLS(listOf(primary.spec.registry!!.host), primary.genericRegistryResourceName)
            )
        }
    }
}
