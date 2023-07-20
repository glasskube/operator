package eu.glasskube.operator.apps.glitchtip

import eu.glasskube.kubernetes.client.patchOrUpdateStatus
import eu.glasskube.operator.Labels
import eu.glasskube.operator.api.reconciler.informerEventSource
import eu.glasskube.operator.api.reconciler.secondaryResource
import eu.glasskube.operator.apps.gitea.dependent.GiteaVolume
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
import eu.glasskube.operator.apps.glitchtip.dependent.GlitchtipVolume
import eu.glasskube.operator.apps.glitchtip.dependent.GlitchtipWorkerDeployment
import eu.glasskube.operator.infra.postgres.PostgresCluster
import eu.glasskube.operator.logger
import io.fabric8.kubernetes.api.model.Service
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration
import io.javaoperatorsdk.operator.api.reconciler.EventSourceContext
import io.javaoperatorsdk.operator.api.reconciler.EventSourceInitializer
import io.javaoperatorsdk.operator.api.reconciler.Reconciler
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl
import io.javaoperatorsdk.operator.api.reconciler.dependent.Dependent

@ControllerConfiguration(
    dependents = [
        Dependent(type = GlitchtipVolume::class, name = "GlitchtipVolume"),
        Dependent(type = GlitchtipSecret::class, name = "GlitchtipSecret"),
        Dependent(type = GlitchtipMinioBucket::class, name = "GlitchtipMinioBucket"),
        Dependent(
            type = GlitchtipPostgresCluster::class,
            name = "GlitchtipPostgresCluster",
            readyPostcondition = GlitchtipPostgresCluster.ReadyPostCondition::class,
            dependsOn = ["GlitchtipMinioBucket"]
        ),
        Dependent(
            type = GlitchtipPostgresBackup::class,
            name = "GlitchtipPostgresBackup",
            dependsOn = ["GlitchtipPostgresCluster"]
        ),
        Dependent(
            type = GlitchtipConfigMap::class,
            name = "GlitchtipConfigMap"
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
            dependsOn = ["GlitchtipDeployment"],
            useEventSourceWithName = GlitchtipReconciler.SERVICE_EVENT_SOURCE
        ),
        Dependent(
            type = GlitchtipIngress::class,
            name = "GlitchtipIngress",
            dependsOn = ["GlitchtipHttpService"]
        )
    ]
)
class GlitchtipReconciler : Reconciler<Glitchtip>, EventSourceInitializer<Glitchtip> {

    override fun reconcile(resource: Glitchtip, context: Context<Glitchtip>): UpdateControl<Glitchtip> {
        log.info("Reconciling ${resource.metadata.name}@${resource.metadata.namespace}")

        return with(context) {
            val deployment: Deployment? by secondaryResource(GlitchtipDeployment.Discriminator())
            val redisDeployment: Deployment? by secondaryResource(GlitchtipRedisDeployment.Discriminator())
            val postgresCluster: PostgresCluster? by secondaryResource()

            resource.patchOrUpdateStatus(
                GlitchtipStatus(
                    readyReplicas = deployment?.status?.readyReplicas ?: 0,
                    redisReady = redisDeployment?.status?.readyReplicas?.let { it > 0 } ?: false,
                    postgresReady = postgresCluster?.status?.instances?.let { it > 0 } ?: false
                )
            )
        }
    }

    override fun prepareEventSources(context: EventSourceContext<Glitchtip>) = with(context) {
        mutableMapOf(
            DEPLOYMENT_EVENT_SOURCE to informerEventSource<Deployment>(),
            SERVICE_EVENT_SOURCE to informerEventSource<Service>()

        )
    }

    companion object {
        const val SELECTOR =
            "${Labels.MANAGED_BY_GLASSKUBE},${Labels.PART_OF}=${Glitchtip.APP_NAME},${Labels.NAME}=${Glitchtip.APP_NAME}"
        const val REDIS_SELECTOR =
            "${Labels.MANAGED_BY_GLASSKUBE},${Labels.PART_OF}=${Glitchtip.APP_NAME},${Labels.NAME}=${Glitchtip.Redis.NAME}"

        internal const val DEPLOYMENT_EVENT_SOURCE = "GlitchtipDeploymentEventSource"
        internal const val SERVICE_EVENT_SOURCE = "GlitchtipServiceEventSource"

        private val log = logger()
    }
}
