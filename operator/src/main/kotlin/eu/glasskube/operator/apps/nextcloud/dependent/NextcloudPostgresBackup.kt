package eu.glasskube.operator.apps.nextcloud.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.operator.apps.nextcloud.Nextcloud
import eu.glasskube.operator.apps.nextcloud.NextcloudReconciler
import eu.glasskube.operator.apps.nextcloud.databaseClusterName
import eu.glasskube.operator.apps.nextcloud.genericResourceName
import eu.glasskube.operator.apps.nextcloud.resourceLabels
import eu.glasskube.operator.infra.postgres.ScheduledBackup
import eu.glasskube.operator.infra.postgres.ScheduledBackupSpec
import eu.glasskube.operator.infra.postgres.scheduledBackup
import io.fabric8.kubernetes.api.model.LocalObjectReference
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = NextcloudReconciler.SELECTOR)
class NextcloudPostgresBackup : CRUDKubernetesDependentResource<ScheduledBackup, Nextcloud>(ScheduledBackup::class.java) {
    override fun desired(primary: Nextcloud, context: Context<Nextcloud>) = scheduledBackup {
        metadata {
            name = primary.genericResourceName
            namespace = primary.namespace
            labels = primary.resourceLabels
        }
        spec = ScheduledBackupSpec(
            schedule = "0 0 3 * * *",
            cluster = LocalObjectReference(primary.databaseClusterName)
        )
    }
}
