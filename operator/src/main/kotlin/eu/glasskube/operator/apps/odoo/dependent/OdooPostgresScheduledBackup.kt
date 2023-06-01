package eu.glasskube.operator.apps.odoo.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.apps.odoo.Odoo
import eu.glasskube.operator.apps.odoo.OdooReconciler
import eu.glasskube.operator.apps.odoo.dbName
import eu.glasskube.operator.apps.odoo.resourceLabels
import eu.glasskube.operator.infra.postgres.ScheduledBackup
import eu.glasskube.operator.infra.postgres.ScheduledBackupSpec
import eu.glasskube.operator.infra.postgres.scheduledBackup
import io.fabric8.kubernetes.api.model.LocalObjectReference
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = OdooReconciler.SELECTOR)
class OdooPostgresScheduledBackup : CRUDKubernetesDependentResource<ScheduledBackup, Odoo>(ScheduledBackup::class.java) {
    override fun desired(primary: Odoo, context: Context<Odoo>) = scheduledBackup {
        metadata {
            name = primary.dbName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        spec = ScheduledBackupSpec(
            schedule = "0 0 3 * * *", // every day at 3:00
            cluster = LocalObjectReference(primary.dbName)
        )
    }
}
