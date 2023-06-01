package eu.glasskube.operator.apps.gitea.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.apps.gitea.Gitea
import eu.glasskube.operator.apps.gitea.GiteaReconciler
import eu.glasskube.operator.apps.gitea.dbClusterName
import eu.glasskube.operator.apps.gitea.resourceLabels
import eu.glasskube.operator.infra.postgres.ScheduledBackup
import eu.glasskube.operator.infra.postgres.ScheduledBackupSpec
import eu.glasskube.operator.infra.postgres.scheduledBackup
import io.fabric8.kubernetes.api.model.LocalObjectReference
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = GiteaReconciler.SELECTOR)
class GiteaPostgresBackup : CRUDKubernetesDependentResource<ScheduledBackup, Gitea>(ScheduledBackup::class.java) {
    override fun desired(primary: Gitea, context: Context<Gitea>) = scheduledBackup {
        metadata {
            name = primary.dbClusterName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        spec = ScheduledBackupSpec(
            schedule = "0 0 3 * * *",
            cluster = LocalObjectReference(primary.dbClusterName)
        )
    }
}
