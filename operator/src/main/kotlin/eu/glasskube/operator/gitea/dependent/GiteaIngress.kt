package eu.glasskube.operator.gitea.dependent

import eu.glasskube.kubernetes.api.model.extensions.ingress
import eu.glasskube.kubernetes.api.model.extensions.ingressBackend
import eu.glasskube.kubernetes.api.model.extensions.ingressPath
import eu.glasskube.kubernetes.api.model.extensions.ingressRule
import eu.glasskube.kubernetes.api.model.extensions.ingressRuleValue
import eu.glasskube.kubernetes.api.model.extensions.spec
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.config.ConfigService
import eu.glasskube.operator.generic.dependent.DependentIngress
import eu.glasskube.operator.gitea.Gitea
import eu.glasskube.operator.gitea.GiteaReconciler
import eu.glasskube.operator.gitea.genericResourceName
import eu.glasskube.operator.gitea.httpServiceName
import eu.glasskube.operator.gitea.resourceLabels
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = GiteaReconciler.SELECTOR)
class GiteaIngress(configService: ConfigService) : DependentIngress<Gitea>(configService) {
    override fun desired(primary: Gitea, context: Context<Gitea>) = ingress {
        metadata {
            name = primary.genericResourceName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
            annotations = defaultAnnotations + ("nginx.ingress.kubernetes.io/proxy-body-size" to "256m")
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
                            backend = ingressBackend(primary.httpServiceName, 3000)
                        )
                    )
                }
            )
        }
    }
}
