package eu.glasskube.operator.apps.odoo

import eu.glasskube.kubernetes.client.patchOrUpdateResourceAndStatus
import eu.glasskube.kubernetes.client.patchOrUpdateStatus
import eu.glasskube.operator.apps.odoo.dependent.OdooConfigMap
import eu.glasskube.operator.apps.odoo.dependent.OdooDatabaseBackupSecret
import eu.glasskube.operator.apps.odoo.dependent.OdooDeployment
import eu.glasskube.operator.apps.odoo.dependent.OdooIngress
import eu.glasskube.operator.apps.odoo.dependent.OdooMinioBucket
import eu.glasskube.operator.apps.odoo.dependent.OdooPersistentVolumeClaim
import eu.glasskube.operator.apps.odoo.dependent.OdooPostgresCluster
import eu.glasskube.operator.apps.odoo.dependent.OdooPostgresScheduledBackup
import eu.glasskube.operator.apps.odoo.dependent.OdooService
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration
import io.javaoperatorsdk.operator.api.reconciler.Reconciler
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl
import io.javaoperatorsdk.operator.api.reconciler.dependent.Dependent

@ControllerConfiguration(
    dependents = [
        Dependent(
            type = OdooDeployment::class,
            dependsOn = ["OdooPostgresCluster", "OdooConfigMap", "OdooPersistentVolumeClaim"]
        ),
        Dependent(
            name = "OdooConfigMap",
            type = OdooConfigMap::class
        ),
        Dependent(
            name = "OdooPersistentVolumeClaim",
            type = OdooPersistentVolumeClaim::class
        ),
        Dependent(type = OdooService::class),
        Dependent(type = OdooIngress::class),
        Dependent(
            type = OdooPostgresScheduledBackup::class,
            dependsOn = ["OdooPostgresCluster"]
        ),
        Dependent(
            name = "OdooPostgresCluster",
            type = OdooPostgresCluster::class
        ),
        Dependent(
            type = OdooMinioBucket::class,
            name = "OdooMinioBucket",
            dependsOn = ["OdooDatabaseBackupSecret"],
            reconcilePrecondition = OdooMinioBucket.ReconcilePrecondition::class
        ),
        Dependent(
            type = OdooDatabaseBackupSecret::class,
            name = "OdooDatabaseBackupSecret",
            reconcilePrecondition = OdooDatabaseBackupSecret.ReconcilePrecondition::class
        )
    ]
)
class OdooReconciler : Reconciler<Odoo> {
    override fun reconcile(resource: Odoo, context: Context<Odoo>): UpdateControl<Odoo> {
        check(resource.status?.demoEnabledOnInstall != !resource.spec.demoEnabled) {
            "demoEnabled can not be altered after first reconciliation"
        }

        return with(resource) {
            if (FINALIZER in metadata.finalizers) {
                metadata.finalizers.remove(FINALIZER)
                patchOrUpdateResourceAndStatus(newStatus)
            } else {
                patchOrUpdateStatus(newStatus)
            }
        }
    }

    private val Odoo.newStatus get() = OdooStatus(demoEnabledOnInstall = spec.demoEnabled)

    companion object {
        const val LABEL = "glasskube.eu/Odoo"
        const val APP_NAME = "odoo"
        const val SELECTOR = "app.kubernetes.io/managed-by=glasskube-operator,app=$APP_NAME"
        private const val FINALIZER = "odoos.glasskube.eu/finalizer"
    }
}
