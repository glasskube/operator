package eu.glasskube.operator.apps.nextcloud.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.kubernetes.api.model.persistentVolumeClaim
import eu.glasskube.kubernetes.api.model.resources
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.operator.apps.nextcloud.Nextcloud
import eu.glasskube.operator.apps.nextcloud.NextcloudReconciler
import eu.glasskube.operator.apps.nextcloud.resourceLabels
import eu.glasskube.operator.apps.nextcloud.volumeName
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim
import io.fabric8.kubernetes.api.model.Quantity
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = NextcloudReconciler.SELECTOR)
class NextcloudVolume :
    CRUDKubernetesDependentResource<PersistentVolumeClaim, Nextcloud>(PersistentVolumeClaim::class.java) {
    override fun desired(primary: Nextcloud, context: Context<Nextcloud>) = persistentVolumeClaim {
        metadata {
            name = primary.volumeName
            namespace = primary.namespace
            labels = primary.resourceLabels
        }
        spec {
            resources {
                requests = mapOf(
                    "storage" to Quantity("20", "Gi")
                )
            }
            accessModes = listOf("ReadWriteOnce")
        }
    }
}
