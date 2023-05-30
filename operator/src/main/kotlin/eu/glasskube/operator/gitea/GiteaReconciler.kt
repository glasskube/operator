package eu.glasskube.operator.gitea

import eu.glasskube.kubernetes.client.patchOrUpdateStatus
import eu.glasskube.operator.Labels
import eu.glasskube.operator.api.reconciler.informerEventSource
import eu.glasskube.operator.api.reconciler.secondaryResource
import eu.glasskube.operator.gitea.dependent.GiteaConfigMap
import eu.glasskube.operator.gitea.dependent.GiteaDeployment
import eu.glasskube.operator.gitea.dependent.GiteaHttpService
import eu.glasskube.operator.gitea.dependent.GiteaIngress
import eu.glasskube.operator.gitea.dependent.GiteaIniConfigMap
import eu.glasskube.operator.gitea.dependent.GiteaMinioBucket
import eu.glasskube.operator.gitea.dependent.GiteaPostgresBackup
import eu.glasskube.operator.gitea.dependent.GiteaPostgresCluster
import eu.glasskube.operator.gitea.dependent.GiteaRedisDeployment
import eu.glasskube.operator.gitea.dependent.GiteaRedisService
import eu.glasskube.operator.gitea.dependent.GiteaSSHService
import eu.glasskube.operator.gitea.dependent.GiteaSecret
import eu.glasskube.operator.gitea.dependent.GiteaServiceMonitor
import eu.glasskube.operator.gitea.dependent.GiteaVolume
import eu.glasskube.operator.logger
import eu.glasskube.operator.postgres.PostgresCluster
import io.fabric8.kubernetes.api.model.ConfigMap
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
        Dependent(type = GiteaVolume::class, name = "GiteaVolume"),
        Dependent(type = GiteaSecret::class, name = "GiteaSecret"),
        Dependent(type = GiteaMinioBucket::class, name = "GiteaMinioBucket"),
        Dependent(
            type = GiteaPostgresCluster::class,
            name = "GiteaPostgresCluster",
            dependsOn = ["GiteaMinioBucket"]
        ),
        Dependent(
            type = GiteaPostgresBackup::class,
            name = "GiteaPostgresBackup",
            dependsOn = ["GiteaPostgresCluster"]
        ),
        Dependent(
            type = GiteaConfigMap::class,
            name = "GiteaConfigMap",
            useEventSourceWithName = GiteaReconciler.CONFIG_EVENT_SOURCE_NAME
        ),
        Dependent(
            type = GiteaIniConfigMap::class,
            name = "GiteaIniConfigMap",
            useEventSourceWithName = GiteaReconciler.CONFIG_EVENT_SOURCE_NAME
        ),
        Dependent(
            type = GiteaRedisDeployment::class,
            name = "GiteaRedisDeployment",
            useEventSourceWithName = GiteaReconciler.DEPLOYMENT_EVENT_SOURCE
        ),
        Dependent(
            type = GiteaRedisService::class,
            name = "GiteaRedisService",
            dependsOn = ["GiteaRedisDeployment"],
            useEventSourceWithName = GiteaReconciler.SERVICE_EVENT_SOURCE
        ),
        Dependent(
            type = GiteaDeployment::class,
            name = "GiteaDeployment",
            dependsOn = ["GiteaPostgresCluster", "GiteaVolume", "GiteaSecret", "GiteaConfigMap", "GiteaIniConfigMap", "GiteaRedisService"],
            useEventSourceWithName = GiteaReconciler.DEPLOYMENT_EVENT_SOURCE
        ),
        Dependent(
            type = GiteaHttpService::class,
            name = "GiteaHttpService",
            dependsOn = ["GiteaDeployment"],
            useEventSourceWithName = GiteaReconciler.SERVICE_EVENT_SOURCE
        ),
        Dependent(
            type = GiteaSSHService::class,
            name = "GiteaSSHService",
            dependsOn = ["GiteaDeployment"],
            useEventSourceWithName = GiteaReconciler.SERVICE_EVENT_SOURCE,
            reconcilePrecondition = GiteaSSHService.ReconcileCondition::class
        ),
        Dependent(
            type = GiteaIngress::class,
            name = "GiteaIngress",
            dependsOn = ["GiteaHttpService"]
        ),
        Dependent(
            type = GiteaServiceMonitor::class,
            name = "GiteaServiceMonitor",
            dependsOn = ["GiteaHttpService"]
        )
    ]
)
class GiteaReconciler : Reconciler<Gitea>, EventSourceInitializer<Gitea> {

    override fun reconcile(resource: Gitea, context: Context<Gitea>): UpdateControl<Gitea> {
        log.info("Reconciling ${resource.metadata.name}@${resource.metadata.namespace}")

        return with(context) {
            val deployment: Deployment? by secondaryResource(GiteaDeployment.Discriminator())
            val redisDeployment: Deployment? by secondaryResource(GiteaRedisDeployment.Discriminator())
            val postgresCluster: PostgresCluster? by secondaryResource()

            resource.patchOrUpdateStatus(
                GiteaStatus(
                    readyReplicas = deployment?.status?.readyReplicas ?: 0,
                    redisReady = redisDeployment?.status?.readyReplicas?.let { it > 0 } ?: false,
                    postgresReady = postgresCluster?.status?.instances?.let { it > 0 } ?: false
                )
            )
        }
    }

    override fun prepareEventSources(context: EventSourceContext<Gitea>) = with(context) {
        mutableMapOf(
            CONFIG_EVENT_SOURCE_NAME to informerEventSource<ConfigMap>(),
            SERVICE_EVENT_SOURCE to informerEventSource<Service>(),
            DEPLOYMENT_EVENT_SOURCE to informerEventSource<Deployment>()
        )
    }

    companion object {
        const val SELECTOR =
            "${Labels.MANAGED_BY_GLASSKUBE},${Labels.PART_OF}=${Gitea.APP_NAME},${Labels.NAME}=${Gitea.APP_NAME}"
        const val REDIS_SELECTOR =
            "${Labels.MANAGED_BY_GLASSKUBE},${Labels.PART_OF}=${Gitea.APP_NAME},${Labels.NAME}=${Gitea.REDIS_NAME}"

        internal const val CONFIG_EVENT_SOURCE_NAME = "GiteaConfigMapEventSource"
        internal const val SERVICE_EVENT_SOURCE = "GiteaServiceEventSource"
        internal const val DEPLOYMENT_EVENT_SOURCE = "GiteaDeploymentEventSource"

        private val log = logger()
    }
}
