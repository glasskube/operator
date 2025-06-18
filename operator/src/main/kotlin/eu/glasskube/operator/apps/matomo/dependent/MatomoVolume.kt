package eu.glasskube.operator.apps.matomo.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.persistentVolumeClaim
import eu.glasskube.kubernetes.api.model.resources
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.operator.apps.matomo.Matomo
import eu.glasskube.operator.apps.matomo.MatomoReconciler
import eu.glasskube.operator.apps.matomo.resourceLabels
import eu.glasskube.operator.apps.matomo.volumeName
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim
import io.fabric8.kubernetes.api.model.Quantity
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = MatomoReconciler.SELECTOR)
class MatomoVolume : CRUDKubernetesDependentResource<PersistentVolumeClaim, Matomo>(PersistentVolumeClaim::class.java) {

    override fun desired(primary: Matomo, context: Context<Matomo>) = persistentVolumeClaim {
        metadata {
            name(primary.volumeName)
            namespace(primary.metadata.namespace)
            labels(primary.resourceLabels)
        }
        spec {
            resources {
                requests = mapOf(
                    "storage" to (primary.spec.storage?.size ?: Quantity("10", "Gi"))
                )
            }
            primary.spec.storage?.storageClassName?.let {
                storageClassName = it
            }
            accessModes = listOf("ReadWriteOnce")
        }
    }
}
