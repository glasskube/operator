package eu.glasskube.operator.apps.gitlab.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.persistentVolumeClaim
import eu.glasskube.kubernetes.api.model.resources
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.operator.apps.gitlab.Gitlab
import eu.glasskube.operator.apps.gitlab.GitlabReconciler
import eu.glasskube.operator.apps.gitlab.resourceLabels
import eu.glasskube.operator.apps.gitlab.volumeName
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim
import io.fabric8.kubernetes.api.model.Quantity
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = GitlabReconciler.SELECTOR)
class GitlabVolume : CRUDKubernetesDependentResource<PersistentVolumeClaim, Gitlab>(PersistentVolumeClaim::class.java) {
    override fun desired(primary: Gitlab, context: Context<Gitlab>) = persistentVolumeClaim {
        metadata {
            name(primary.volumeName)
            namespace(primary.metadata.namespace)
            labels(primary.resourceLabels)
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
