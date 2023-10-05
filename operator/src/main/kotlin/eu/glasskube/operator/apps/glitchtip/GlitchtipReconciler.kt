package eu.glasskube.operator.apps.glitchtip

import eu.glasskube.kubernetes.client.patchOrUpdateStatus
import eu.glasskube.operator.Labels
import eu.glasskube.operator.api.reconciler.getSecondaryResource
import eu.glasskube.operator.api.reconciler.informerEventSource
import eu.glasskube.operator.apps.glitchtip.dependent.GlitchtipBinConfigMap
import eu.glasskube.operator.apps.glitchtip.dependent.GlitchtipConfigMap
import eu.glasskube.operator.apps.glitchtip.dependent.GlitchtipDeployment
import eu.glasskube.operator.apps.glitchtip.dependent.GlitchtipHttpService
import eu.glasskube.operator.apps.glitchtip.dependent.GlitchtipIngress
import eu.glasskube.operator.apps.glitchtip.dependent.GlitchtipMinioBucket
import eu.glasskube.operator.apps.glitchtip.dependent.GlitchtipPostgresBackup
import eu.glasskube.operator.apps.glitchtip.dependent.GlitchtipPostgresCluster
import eu.glasskube.operator.apps.glitchtip.dependent.GlitchtipRedisDeployment
import eu.glasskube.operator.apps.glitchtip.dependent.GlitchtipRedisService
import eu.glasskube.operator.apps.glitchtip.dependent.GlitchtipSecret
import eu.glasskube.operator.apps.glitchtip.dependent.GlitchtipVeleroBackupStorageLocation
import eu.glasskube.operator.apps.glitchtip.dependent.GlitchtipVeleroSchedule
import eu.glasskube.operator.apps.glitchtip.dependent.GlitchtipVeleroSecret
import eu.glasskube.operator.apps.glitchtip.dependent.GlitchtipVolume
import eu.glasskube.operator.apps.glitchtip.dependent.GlitchtipWorkerDeployment
import eu.glasskube.operator.generic.BaseReconciler
import eu.glasskube.operator.generic.condition.isReady
import eu.glasskube.operator.infra.postgres.PostgresCluster
import eu.glasskube.operator.infra.postgres.isReady
import eu.glasskube.operator.processing.CompositeSecondaryToPrimaryMapper
import eu.glasskube.operator.webhook.WebhookService
import eu.glasskube.utils.logger
import io.fabric8.kubernetes.api.model.ConfigMap
import io.fabric8.kubernetes.api.model.Secret
import io.fabric8.kubernetes.api.model.Service
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration
import io.javaoperatorsdk.operator.api.reconciler.EventSourceContext
import io.javaoperatorsdk.operator.api.reconciler.EventSourceInitializer
import io.javaoperatorsdk.operator.api.reconciler.dependent.Dependent
import io.javaoperatorsdk.operator.processing.event.source.informer.Mappers
import kotlin.jvm.optionals.getOrDefault

@ControllerConfiguration(
    dependents = [
        Dependent(type = GlitchtipVolume::class, name = "GlitchtipVolume"),
        Dependent(
            type = GlitchtipSecret::class,
            name = "GlitchtipSecret",
            useEventSourceWithName = GlitchtipReconciler.SECRET_EVENT_SOURCE
        ),
        Dependent(
            type = GlitchtipMinioBucket::class,
            name = "GlitchtipMinioBucket",
            reconcilePrecondition = GlitchtipMinioBucket.ReconcilePrecondition::class
        ),
        Dependent(
            type = GlitchtipPostgresCluster::class,
            name = "GlitchtipPostgresCluster",
            readyPostcondition = GlitchtipPostgresCluster.ReadyPostCondition::class
        ),
        Dependent(
            type = GlitchtipPostgresBackup::class,
            name = "GlitchtipPostgresBackup",
            dependsOn = ["GlitchtipPostgresCluster"]
        ),
        Dependent(
            type = GlitchtipConfigMap::class,
            name = "GlitchtipConfigMap",
            useEventSourceWithName = GlitchtipReconciler.CONFIGMAP_EVENT_SOURCE
        ),
        Dependent(
            type = GlitchtipBinConfigMap::class,
            name = "GlitchtipBinConfigMap",
            useEventSourceWithName = GlitchtipReconciler.CONFIGMAP_EVENT_SOURCE
        ),
        Dependent(
            type = GlitchtipRedisDeployment::class,
            name = "GlitchtipRedisDeployment",
            useEventSourceWithName = GlitchtipReconciler.DEPLOYMENT_EVENT_SOURCE
        ),
        Dependent(
            type = GlitchtipRedisService::class,
            name = "GlitchtipRedisService",
            dependsOn = ["GlitchtipRedisDeployment"],
            useEventSourceWithName = GlitchtipReconciler.SERVICE_EVENT_SOURCE
        ),
        Dependent(
            type = GlitchtipDeployment::class,
            name = "GlitchtipDeployment",
            dependsOn = ["GlitchtipPostgresCluster", "GlitchtipConfigMap", "GlitchtipSecret", "GlitchtipRedisService", "GlitchtipVolume"],
            readyPostcondition = GlitchtipDeployment.ReadyPostCondition::class,
            useEventSourceWithName = GlitchtipReconciler.DEPLOYMENT_EVENT_SOURCE
        ),
        Dependent(
            type = GlitchtipWorkerDeployment::class,
            name = "GlitchtipWorkerDeployment",
            dependsOn = ["GlitchtipDeployment"],
            useEventSourceWithName = GlitchtipReconciler.DEPLOYMENT_EVENT_SOURCE
        ),
        Dependent(
            type = GlitchtipHttpService::class,
            name = "GlitchtipHttpService",
            useEventSourceWithName = GlitchtipReconciler.SERVICE_EVENT_SOURCE
        ),
        Dependent(
            type = GlitchtipIngress::class,
            name = "GlitchtipIngress"
        ),
        Dependent(
            type = GlitchtipVeleroSecret::class,
            name = "GlitchtipVeleroSecret",
            reconcilePrecondition = GlitchtipVeleroSecret.ReconcilePrecondition::class,
            useEventSourceWithName = GlitchtipReconciler.SECRET_EVENT_SOURCE
        ),
        Dependent(
            type = GlitchtipVeleroBackupStorageLocation::class,
            name = "GlitchtipVeleroBackupStorageLocation",
            dependsOn = ["GlitchtipVeleroSecret"]
        ),
        Dependent(
            type = GlitchtipVeleroSchedule::class,
            name = "GlitchtipVeleroSchedule",
            dependsOn = ["GlitchtipVeleroBackupStorageLocation"]
        )
    ]
)
class GlitchtipReconciler(webhookService: WebhookService) :
    BaseReconciler<Glitchtip>(webhookService), EventSourceInitializer<Glitchtip> {

    override fun processReconciliation(resource: Glitchtip, context: Context<Glitchtip>) = with(context) {
        resource.patchOrUpdateStatus(
            GlitchtipStatus(
                readyReplicas = getSecondaryResource(GlitchtipDeployment.Discriminator())
                    .map { it.status?.readyReplicas ?: 0 }.getOrDefault(0),
                workerReadyReplicas = getSecondaryResource(GlitchtipWorkerDeployment.Discriminator())
                    .map { it.status?.readyReplicas ?: 0 }.getOrDefault(0),
                redisReady = getSecondaryResource(GlitchtipRedisDeployment.Discriminator())
                    .map { it.isReady }.getOrDefault(false),
                postgresReady = getSecondaryResource<PostgresCluster>()
                    .map { it.isReady }.getOrDefault(false)
            )
        )
    }

    override fun prepareEventSources(context: EventSourceContext<Glitchtip>) = with(context) {
        mutableMapOf(
            DEPLOYMENT_EVENT_SOURCE to informerEventSource<Deployment>(),
            SERVICE_EVENT_SOURCE to informerEventSource<Service>(),
            CONFIGMAP_EVENT_SOURCE to informerEventSource<ConfigMap>(),
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
            "${Labels.MANAGED_BY_GLASSKUBE},${Labels.PART_OF}=${Glitchtip.APP_NAME},${Labels.NAME}=${Glitchtip.APP_NAME}"
        const val REDIS_SELECTOR =
            "${Labels.MANAGED_BY_GLASSKUBE},${Labels.PART_OF}=${Glitchtip.APP_NAME},${Labels.NAME}=${Glitchtip.Redis.NAME}"

        internal const val DEPLOYMENT_EVENT_SOURCE = "GlitchtipDeploymentEventSource"
        internal const val SERVICE_EVENT_SOURCE = "GlitchtipServiceEventSource"
        internal const val CONFIGMAP_EVENT_SOURCE = "GlitchtipConfigMapEventSource"
        internal const val SECRET_EVENT_SOURCE = "GlitchtipSecretEventSource"

        private val log = logger()
    }
}
