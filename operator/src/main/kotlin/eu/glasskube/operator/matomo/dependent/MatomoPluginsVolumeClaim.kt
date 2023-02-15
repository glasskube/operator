package eu.glasskube.operator.matomo.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.persistentVolumeClaim
import eu.glasskube.kubernetes.api.model.resources
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.operator.matomo.Matomo
import eu.glasskube.operator.matomo.MatomoReconciler
import eu.glasskube.operator.matomo.persistentVolumeClaimName
import eu.glasskube.operator.matomo.resourceLabels
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim
import io.fabric8.kubernetes.api.model.Quantity
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = MatomoReconciler.SELECTOR)
class MatomoPluginsVolumeClaim :
    CRUDKubernetesDependentResource<PersistentVolumeClaim, Matomo>(PersistentVolumeClaim::class.java) {

    override fun desired(primary: Matomo, context: Context<Matomo>) = persistentVolumeClaim {
        metadata {
            name = primary.persistentVolumeClaimName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        spec {
            accessModes = listOf("ReadWriteOnce")
            resources {
                requests = mapOf(
                    "storage" to Quantity("10", "Gi")
                )
            }
        }
    }
}
