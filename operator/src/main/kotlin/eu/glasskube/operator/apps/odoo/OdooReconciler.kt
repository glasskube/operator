package eu.glasskube.operator.apps.odoo

import eu.glasskube.kubernetes.client.patchOrUpdateStatus
import eu.glasskube.operator.api.reconciler.informerEventSource
import eu.glasskube.operator.apps.odoo.dependent.OdooConfigMap
import eu.glasskube.operator.apps.odoo.dependent.OdooDatabaseBackupSecret
import eu.glasskube.operator.apps.odoo.dependent.OdooDeployment
import eu.glasskube.operator.apps.odoo.dependent.OdooIngress
import eu.glasskube.operator.apps.odoo.dependent.OdooMinioBucket
import eu.glasskube.operator.apps.odoo.dependent.OdooPersistentVolumeClaim
import eu.glasskube.operator.apps.odoo.dependent.OdooPostgresCluster
import eu.glasskube.operator.apps.odoo.dependent.OdooPostgresScheduledBackup
import eu.glasskube.operator.apps.odoo.dependent.OdooService
import eu.glasskube.operator.apps.odoo.dependent.OdooVeleroBackupStorageLocation
import eu.glasskube.operator.apps.odoo.dependent.OdooVeleroSchedule
import eu.glasskube.operator.apps.odoo.dependent.OdooVeleroSecret
import eu.glasskube.operator.generic.BaseReconciler
import eu.glasskube.operator.processing.CompositeSecondaryToPrimaryMapper
import eu.glasskube.operator.webhook.WebhookService
import io.fabric8.kubernetes.api.model.Secret
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration
import io.javaoperatorsdk.operator.api.reconciler.EventSourceContext
import io.javaoperatorsdk.operator.api.reconciler.EventSourceInitializer
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl
import io.javaoperatorsdk.operator.api.reconciler.dependent.Dependent
import io.javaoperatorsdk.operator.processing.event.source.informer.Mappers

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
            reconcilePrecondition = OdooDatabaseBackupSecret.ReconcilePrecondition::class,
            useEventSourceWithName = OdooReconciler.SECRET_EVENT_SOURCE
        ),
        Dependent(
            type = OdooVeleroSecret::class,
            name = "OdooVeleroSecret",
            reconcilePrecondition = OdooVeleroSecret.ReconcilePrecondition::class,
            useEventSourceWithName = OdooReconciler.SECRET_EVENT_SOURCE
        ),
        Dependent(
            type = OdooVeleroBackupStorageLocation::class,
            name = "OdooVeleroBackupStorageLocation",
            dependsOn = ["OdooVeleroSecret"]
        ),
        Dependent(
            type = OdooVeleroSchedule::class,
            name = "OdooVeleroSchedule",
            dependsOn = ["OdooVeleroBackupStorageLocation"]
        )
    ]
)
class OdooReconciler(webhookService: WebhookService) :
    BaseReconciler<Odoo>(webhookService), EventSourceInitializer<Odoo> {

    override fun processReconciliation(resource: Odoo, context: Context<Odoo>): UpdateControl<Odoo> {
        check(resource.status?.demoEnabledOnInstall != !resource.spec.demoEnabled) {
            "demoEnabled can not be altered after first reconciliation"
        }

        return with(resource) {
            patchOrUpdateStatus(newStatus)
        }
    }

    override fun prepareEventSources(context: EventSourceContext<Odoo>) = with(context) {
        mutableMapOf(
            SECRET_EVENT_SOURCE to informerEventSource<Secret>(SELECTOR) {
                withSecondaryToPrimaryMapper(
                    CompositeSecondaryToPrimaryMapper(
                        Mappers.fromOwnerReference(),
                        Mappers.fromDefaultAnnotations()
                    )
                )
            }
        )
    }

    private val Odoo.newStatus get() = OdooStatus(demoEnabledOnInstall = spec.demoEnabled)

    companion object {
        const val LABEL = "glasskube.eu/Odoo"
        const val APP_NAME = "odoo"
        const val SELECTOR = "app.kubernetes.io/managed-by=glasskube-operator,app=$APP_NAME"
        private const val FINALIZER = "odoos.glasskube.eu/finalizer"
        internal const val SECRET_EVENT_SOURCE = "OdooSecretEventSource"
    }
}
