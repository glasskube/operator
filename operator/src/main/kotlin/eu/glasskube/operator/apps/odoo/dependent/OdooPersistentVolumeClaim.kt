package eu.glasskube.operator.apps.odoo.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.persistentVolumeClaim
import eu.glasskube.kubernetes.api.model.resources
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.operator.apps.odoo.Odoo
import eu.glasskube.operator.apps.odoo.OdooReconciler
import eu.glasskube.operator.apps.odoo.resourceLabels
import eu.glasskube.operator.apps.odoo.volumeName
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim
import io.fabric8.kubernetes.api.model.Quantity
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = OdooReconciler.SELECTOR)
class OdooPersistentVolumeClaim :
    CRUDKubernetesDependentResource<PersistentVolumeClaim, Odoo>(PersistentVolumeClaim::class.java) {
    override fun desired(primary: Odoo, context: Context<Odoo>) = persistentVolumeClaim {
        metadata {
            name(primary.volumeName)
            namespace(primary.metadata.namespace)
            labels(primary.resourceLabels)
        }
        spec {
            accessModes = listOf("ReadWriteOnce")
            resources {
                requests = mapOf(
                    "storage" to (primary.spec.storage?.size ?: Quantity("10", "Gi"))
                )
            }
            primary.spec.storage?.storageClassName?.let {
                storageClassName = it
            }
        }
    }
}
