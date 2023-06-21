package eu.glasskube.operator.apps.metabase.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.apps.metabase.Metabase
import eu.glasskube.operator.apps.metabase.MetabaseReconciler
import eu.glasskube.operator.apps.metabase.dbClusterName
import eu.glasskube.operator.apps.metabase.resourceLabels
import eu.glasskube.operator.infra.postgres.ScheduledBackup
import eu.glasskube.operator.infra.postgres.ScheduledBackupSpec
import eu.glasskube.operator.infra.postgres.scheduledBackup
import io.fabric8.kubernetes.api.model.LocalObjectReference
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = MetabaseReconciler.SELECTOR)
class MetabasePostgresBackup : CRUDKubernetesDependentResource<ScheduledBackup, Metabase>(ScheduledBackup::class.java) {
    override fun desired(primary: Metabase, context: Context<Metabase>) = scheduledBackup {
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
