package eu.glasskube.operator.httpecho

import eu.glasskube.kubernetes.api.model.*
import eu.glasskube.kubernetes.api.model.apps.deployment
import eu.glasskube.kubernetes.api.model.apps.deploymentSpec
import eu.glasskube.kubernetes.api.model.extensions.*
import eu.glasskube.operator.resourceLabels
import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.api.model.PodTemplateSpec
import io.fabric8.kubernetes.api.model.Service
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.fabric8.kubernetes.api.model.networking.v1.Ingress
import io.fabric8.kubernetes.api.model.networking.v1.IngressRule
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Version
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

data class HttpEchoSpec(
    var text: String? = null,
    var host: String? = null
)

data class HttpEchoStatus(
    var result: String? = null
)

@Group("glasskube.eu")
@Version("v1alpha1")
class HttpEcho : CustomResource<HttpEchoSpec, HttpEchoStatus>(), Namespaced

val HttpEcho.identifyingLabel
    get() = HttpEchoReconciler.LABEL to metadata.name

val HttpEcho.resourceLabels
    get() = resourceLabels(HttpEchoReconciler.APP_NAME, identifyingLabel)

@KubernetesDependent(labelSelector = HttpEchoReconciler.SELECTOR)
class HttpEchoDeployment : CRUDKubernetesDependentResource<Deployment, HttpEcho>(Deployment::class.java) {
    override fun desired(primary: HttpEcho, context: Context<HttpEcho>) = deployment {
        metadata = objectMeta {
            name = primary.metadata.name
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        spec = deploymentSpec {
            selector = labelSelector { matchLabels = mapOf(primary.identifyingLabel) }
            template = PodTemplateSpec(
                objectMeta {
                    labels = primary.resourceLabels
                },
                podSpec {
                    containers = listOf(
                        container {
                            name = "echo"
                            image = "hashicorp/http-echo:0.2.3"
                            args = listOf("-text=\"${primary.spec.text}\"")
                            ports = listOf(containerPort { containerPort = 80 })
                        }
                    )
                }
            )
        }
    }
}

@KubernetesDependent(labelSelector = HttpEchoReconciler.SELECTOR)
class HttpEchoService : CRUDKubernetesDependentResource<Service, HttpEcho>(Service::class.java) {
    override fun desired(primary: HttpEcho, context: Context<HttpEcho>) = service {
        metadata = objectMeta {
            name = primary.metadata.name
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        spec = serviceSpec {
            selector = mapOf(HttpEchoReconciler.LABEL to primary.metadata.name)
            ports = listOf(servicePort { port = 5678; name = "http" })
        }
    }
}

@KubernetesDependent(labelSelector = HttpEchoReconciler.SELECTOR)
class HttpEchoIngress : CRUDKubernetesDependentResource<Ingress, HttpEcho>(Ingress::class.java) {
    override fun desired(primary: HttpEcho, context: Context<HttpEcho>): Ingress = ingress {
        metadata = objectMeta {
            name = primary.metadata.name
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        spec = ingressSpec {
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
