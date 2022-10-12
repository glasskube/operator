package eu.glasskube.operator.httpecho.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.service
import eu.glasskube.kubernetes.api.model.servicePort
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.operator.httpecho.HttpEcho
import eu.glasskube.operator.httpecho.HttpEchoReconciler
import eu.glasskube.operator.httpecho.resourceLabels
import io.fabric8.kubernetes.api.model.Service
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = HttpEchoReconciler.SELECTOR)
class HttpEchoService : CRUDKubernetesDependentResource<Service, HttpEcho>(Service::class.java) {
    override fun desired(primary: HttpEcho, context: Context<HttpEcho>) = service {
        metadata {
            name = primary.metadata.name
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        spec {
            selector = mapOf(HttpEchoReconciler.LABEL to primary.metadata.name)
            ports = listOf(servicePort { port = 5678; name = "http" })
        }
    }
}
