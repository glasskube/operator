package eu.glasskube.operator.apps.metabase

import eu.glasskube.kubernetes.client.patchOrUpdateStatus
import eu.glasskube.operator.Labels
import eu.glasskube.operator.api.reconciler.getSecondaryResource
import eu.glasskube.operator.api.reconciler.informerEventSource
import eu.glasskube.operator.apps.metabase.dependent.MetabaseConfigMap
import eu.glasskube.operator.apps.metabase.dependent.MetabaseDeployment
import eu.glasskube.operator.apps.metabase.dependent.MetabaseHttpService
import eu.glasskube.operator.apps.metabase.dependent.MetabaseIngress
import eu.glasskube.operator.apps.metabase.dependent.MetabaseMinioBucket
import eu.glasskube.operator.apps.metabase.dependent.MetabasePostgresBackup
import eu.glasskube.operator.apps.metabase.dependent.MetabasePostgresCluster
import eu.glasskube.operator.apps.metabase.dependent.MetabaseSecret
import eu.glasskube.operator.apps.metabase.dependent.MetabaseServiceMonitor
import eu.glasskube.operator.apps.metabase.dependent.MetabaseVeleroBackupStorageLocation
import eu.glasskube.operator.apps.metabase.dependent.MetabaseVeleroSchedule
import eu.glasskube.operator.apps.metabase.dependent.MetabaseVeleroSecret
import eu.glasskube.operator.generic.BaseReconciler
import eu.glasskube.operator.infra.postgres.PostgresCluster
import eu.glasskube.operator.processing.CompositeSecondaryToPrimaryMapper
import eu.glasskube.operator.webhook.WebhookService
import eu.glasskube.utils.logger
import io.fabric8.kubernetes.api.model.Secret
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration
import io.javaoperatorsdk.operator.api.reconciler.EventSourceContext
import io.javaoperatorsdk.operator.api.reconciler.EventSourceInitializer
import io.javaoperatorsdk.operator.api.reconciler.dependent.Dependent
import io.javaoperatorsdk.operator.processing.event.source.informer.Mappers

@ControllerConfiguration(
    dependents = [
        Dependent(
            type = MetabaseSecret::class,
            name = "MetabaseSecret",
            useEventSourceWithName = MetabaseReconciler.SECRET_EVENT_SOURCE
        ),
        Dependent(
            type = MetabaseMinioBucket::class,
            name = "MetabaseMinioBucket",
            reconcilePrecondition = MetabaseMinioBucket.ReconcilePrecondition::class
        ),
        Dependent(
            type = MetabasePostgresCluster::class,
            name = "MetabasePostgresCluster",
            readyPostcondition = MetabasePostgresCluster.ReadyPostCondition::class
        ),
        Dependent(
            type = MetabasePostgresBackup::class,
            name = "MetabasePostgresBackup",
            dependsOn = ["MetabasePostgresCluster"]
        ),
        Dependent(
            type = MetabaseConfigMap::class,
            name = "MetabaseConfigMap"
        ),
        Dependent(
            type = MetabaseDeployment::class,
            name = "MetabaseDeployment",
            dependsOn = ["MetabasePostgresCluster", "MetabaseConfigMap", "MetabaseSecret"]
        ),
        Dependent(
            type = MetabaseHttpService::class,
            name = "MetabaseHttpService",
            dependsOn = ["MetabaseDeployment"]
        ),
        Dependent(
            type = MetabaseIngress::class,
            name = "MetabaseIngress",
            dependsOn = ["MetabaseHttpService"]
        ),
        Dependent(
            type = MetabaseServiceMonitor::class,
            name = "MetabaseServiceMonitor",
            dependsOn = ["MetabaseHttpService"]
        ),
        Dependent(
            type = MetabaseVeleroSecret::class,
            name = "MetabaseVeleroSecret",
            reconcilePrecondition = MetabaseVeleroSecret.ReconcilePrecondition::class,
            useEventSourceWithName = MetabaseReconciler.SECRET_EVENT_SOURCE
        ),
        Dependent(
            type = MetabaseVeleroBackupStorageLocation::class,
            name = "MetabaseVeleroBackupStorageLocation",
            dependsOn = ["MetabaseVeleroSecret"]
        ),
        Dependent(
            type = MetabaseVeleroSchedule::class,
            name = "MetabaseVeleroSchedule",
            dependsOn = ["MetabaseVeleroSecret"]
        )
    ]
)
class MetabaseReconciler(webhookService: WebhookService) :
    BaseReconciler<Metabase>(webhookService), EventSourceInitializer<Metabase> {

    override fun processReconciliation(resource: Metabase, context: Context<Metabase>) = with(context) {
        resource.patchOrUpdateStatus(
            MetabaseStatus(
                getSecondaryResource<Deployment>().map { it.status?.readyReplicas ?: 0 }.orElse(0),
                getSecondaryResource<PostgresCluster>().map { it.status?.readyInstances?.let { it > 0 } }.orElse(false)
            )
        )
    }

    override fun prepareEventSources(context: EventSourceContext<Metabase>) = with(context) {
        mutableMapOf(
            SECRET_EVENT_SOURCE to informerEventSource<Secret> {
                withSecondaryToPrimaryMapper(
                    CompositeSecondaryToPrimaryMapper(
                        Mappers.fromOwnerReference(),
                        Mappers.fromDefaultAnnotations()
                    )
                )
            }
        )
    }

    companion object {
        const val SELECTOR =
            "${Labels.MANAGED_BY_GLASSKUBE},${Labels.PART_OF}=${Metabase.APP_NAME},${Labels.NAME}=${Metabase.APP_NAME}"

        internal const val SECRET_EVENT_SOURCE = "MetabaseSecretEventSource"

        @JvmStatic
        private val log = logger()
    }
}
