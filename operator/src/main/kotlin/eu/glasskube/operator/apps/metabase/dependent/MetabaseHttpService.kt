package eu.glasskube.operator.apps.metabase.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.service
import eu.glasskube.kubernetes.api.model.servicePort
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.operator.apps.metabase.Metabase
import eu.glasskube.operator.apps.metabase.MetabaseReconciler
import eu.glasskube.operator.apps.metabase.httpServiceName
import eu.glasskube.operator.apps.metabase.resourceLabelSelector
import eu.glasskube.operator.apps.metabase.resourceLabels
import io.fabric8.kubernetes.api.model.Service
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = MetabaseReconciler.SELECTOR)
class MetabaseHttpService : CRUDKubernetesDependentResource<Service, Metabase>(Service::class.java) {

    override fun desired(primary: Metabase, context: Context<Metabase>) = service {
        metadata {
            name(primary.httpServiceName)
            namespace(primary.metadata.namespace)
            labels(primary.resourceLabels)
        }
        spec {
            type = "ClusterIP"
            selector = primary.resourceLabelSelector
            ports = listOf(
                servicePort {
                    port = 3000
                    name = "http"
                },
                servicePort {
                    port = 9191
                    name = "metabase-exp"
                }
            )
        }
    }
}
