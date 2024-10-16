package eu.glasskube.operator.apps.odoo.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.service
import eu.glasskube.kubernetes.api.model.servicePort
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.operator.apps.odoo.Odoo
import eu.glasskube.operator.apps.odoo.OdooReconciler
import eu.glasskube.operator.apps.odoo.resourceLabels
import eu.glasskube.operator.apps.odoo.serviceName
import io.fabric8.kubernetes.api.model.Service
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = OdooReconciler.SELECTOR)
class OdooService : CRUDKubernetesDependentResource<Service, Odoo>(Service::class.java) {
    override fun desired(primary: Odoo, context: Context<Odoo>) = service {
        metadata {
            name(primary.serviceName)
            namespace(primary.metadata.namespace)
            labels(primary.resourceLabels)
        }
        spec {
            selector = mapOf(OdooReconciler.LABEL to primary.metadata.name)
            ports = listOf(
                servicePort {
                    port = 8069
                    name = "http"
                }
            )
        }
    }
}
