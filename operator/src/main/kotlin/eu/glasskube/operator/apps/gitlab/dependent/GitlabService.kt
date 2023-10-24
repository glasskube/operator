package eu.glasskube.operator.apps.gitlab.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.kubernetes.api.model.service
import eu.glasskube.kubernetes.api.model.servicePort
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.operator.apps.gitlab.Gitlab
import eu.glasskube.operator.apps.gitlab.GitlabReconciler
import eu.glasskube.operator.apps.gitlab.resourceLabelSelector
import eu.glasskube.operator.apps.gitlab.resourceLabels
import eu.glasskube.operator.apps.gitlab.serviceName
import io.fabric8.kubernetes.api.model.Service
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(
    labelSelector = GitlabReconciler.SELECTOR,
    resourceDiscriminator = GitlabService.Discriminator::class
)
class GitlabService : CRUDKubernetesDependentResource<Service, Gitlab>(Service::class.java) {
    internal class Discriminator :
        ResourceIDMatcherDiscriminator<Service, Gitlab>({ ResourceID(it.serviceName, it.namespace) })

    override fun desired(primary: Gitlab, context: Context<Gitlab>) = service {
        metadata {
            name(primary.serviceName)
            namespace(primary.metadata.namespace)
            labels(primary.resourceLabels)
        }
        spec {
            type = "ClusterIP"
            selector = primary.resourceLabelSelector
            ports = listOfNotNull(
                servicePort {
                    port = 22
                    name = "ssh"
                }.takeIf { primary.spec.sshEnabled },
                servicePort {
                    port = 80
                    name = "http"
                },
                primary.spec.registry?.let {
                    servicePort {
                        port = 5000
                        name = "registry"
                    }
                    servicePort {
                        port = 5443
                        name = "registry-ssl"
                    }
                },
                servicePort {
                    port = 8082
                    name = "sidekiq-exp"
                },
                servicePort {
                    port = 9168
                    name = "gitlab-exp"
                },
                servicePort {
                    port = 9236
                    name = "gitaly-exp"
                }
            )
        }
    }
}
