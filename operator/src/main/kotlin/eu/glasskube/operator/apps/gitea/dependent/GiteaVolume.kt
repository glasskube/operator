package eu.glasskube.operator.apps.gitea.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.persistentVolumeClaim
import eu.glasskube.kubernetes.api.model.resources
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.operator.api.reconciler.getSecondaryResource
import eu.glasskube.operator.apps.gitea.Gitea
import eu.glasskube.operator.apps.gitea.GiteaReconciler
import eu.glasskube.operator.apps.gitea.genericResourceName
import eu.glasskube.operator.apps.gitea.resourceLabels
import eu.glasskube.utils.logger
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim
import io.fabric8.kubernetes.api.model.Quantity
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = GiteaReconciler.SELECTOR)
class GiteaVolume : CRUDKubernetesDependentResource<PersistentVolumeClaim, Gitea>(PersistentVolumeClaim::class.java) {

    override fun desired(primary: Gitea, context: Context<Gitea>) = persistentVolumeClaim {
        metadata {
            name(primary.genericResourceName)
            namespace(primary.metadata.namespace)
            labels(primary.resourceLabels)
        }
        spec {
            resources {
                requests = mapOf(
                    "storage" to (primary.spec.storage?.size ?: Quantity("10", "Gi"))
                )
            }
            storageClassName = primary.spec.storage?.storageClassName
            accessModes = listOf(primary.spec.storage?.accessMode ?: "ReadWriteMany")
        }
    }

    override fun onUpdated(
        primary: Gitea,
        updated: PersistentVolumeClaim,
        actual: PersistentVolumeClaim,
        context: Context<Gitea>
    ) {
        super.onUpdated(primary, updated, actual, context)
        if (updated.spec.resources.requests != actual.spec.resources.requests) {
            context.getSecondaryResource(GiteaDeployment.Discriminator()).ifPresent {
                log.info("Restarting deployment after PVC resize")
                Thread.sleep(1000)
                context.client.apps().deployments().resource(it).rolling().restart()
            }
        }
    }

    companion object {
        private val log = logger()
    }
}
