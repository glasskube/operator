package eu.glasskube.operator.apps.gitea.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.persistentVolumeClaim
import eu.glasskube.kubernetes.api.model.resources
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.operator.apps.gitea.Gitea
import eu.glasskube.operator.apps.gitea.GiteaReconciler
import eu.glasskube.operator.apps.gitea.genericResourceName
import eu.glasskube.operator.apps.gitea.resourceLabels
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim
import io.fabric8.kubernetes.api.model.Quantity
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = GiteaReconciler.SELECTOR)
class GiteaVolume : CRUDKubernetesDependentResource<PersistentVolumeClaim, Gitea>(PersistentVolumeClaim::class.java) {

    override fun desired(primary: Gitea, context: Context<Gitea>) = persistentVolumeClaim {
        metadata {
            name = primary.genericResourceName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        spec {
            resources {
                requests = mapOf(
                    "storage" to Quantity("10", "Gi")
                )
            }
            accessModes = listOf("ReadWriteMany")
        }
    }
}
