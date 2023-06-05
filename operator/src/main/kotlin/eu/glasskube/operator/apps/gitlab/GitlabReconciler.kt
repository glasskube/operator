package eu.glasskube.operator.apps.gitlab

import eu.glasskube.kubernetes.client.patchOrUpdateStatus
import eu.glasskube.operator.Labels
import eu.glasskube.operator.api.reconciler.getSecondaryResource
import eu.glasskube.operator.api.reconciler.informerEventSource
import eu.glasskube.operator.apps.gitlab.dependent.GitlabConfigMap
import eu.glasskube.operator.apps.gitlab.dependent.GitlabDeployment
import eu.glasskube.operator.apps.gitlab.dependent.GitlabIngress
import eu.glasskube.operator.apps.gitlab.dependent.GitlabMinioBucket
import eu.glasskube.operator.apps.gitlab.dependent.GitlabPostgresBackup
import eu.glasskube.operator.apps.gitlab.dependent.GitlabPostgresCluster
import eu.glasskube.operator.apps.gitlab.dependent.GitlabSSHService
import eu.glasskube.operator.apps.gitlab.dependent.GitlabService
import eu.glasskube.operator.apps.gitlab.dependent.GitlabServiceMonitor
import eu.glasskube.operator.apps.gitlab.dependent.GitlabVolume
import eu.glasskube.operator.infra.postgres.PostgresCluster
import eu.glasskube.operator.logger
import io.fabric8.kubernetes.api.model.Service
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration
import io.javaoperatorsdk.operator.api.reconciler.EventSourceContext
import io.javaoperatorsdk.operator.api.reconciler.EventSourceInitializer
import io.javaoperatorsdk.operator.api.reconciler.Reconciler
import io.javaoperatorsdk.operator.api.reconciler.dependent.Dependent

@ControllerConfiguration(
    dependents = [
        Dependent(type = GitlabMinioBucket::class, name = "GitlabMinioBucket"),
        Dependent(type = GitlabConfigMap::class, name = "GitlabConfigMap"),
        Dependent(type = GitlabVolume::class, name = "GitlabVolume"),
        Dependent(
            type = GitlabPostgresCluster::class,
            name = "GitlabPostgresCluster",
            readyPostcondition = GitlabPostgresCluster.ReadyPostCondition::class
        ),
        Dependent(
            type = GitlabPostgresBackup::class,
            name = "GitlabPostgresBackup",
            dependsOn = ["GitlabMinioBucket"]
        ),
        Dependent(
            type = GitlabDeployment::class,
            name = "GitlabDeployment",
            dependsOn = ["GitlabVolume", "GitlabConfigMap", "GitlabPostgresCluster"]
        ),
        Dependent(
            type = GitlabService::class,
            name = "GitlabService",
            useEventSourceWithName = GitlabReconciler.SERVICE_EVENT_SOURCE
        ),
        Dependent(
            type = GitlabSSHService::class,
            name = "GitlabSSHService",
            useEventSourceWithName = GitlabReconciler.SERVICE_EVENT_SOURCE,
            reconcilePrecondition = GitlabSSHService.ReconcileCondition::class
        ),
        Dependent(
            type = GitlabServiceMonitor::class,
            name = "GitlabServiceMonitor",
            dependsOn = ["GitlabService"]
        ),
        Dependent(
            type = GitlabIngress::class,
            name = "GitlabIngress",
            dependsOn = ["GitlabService"]
        )
    ]
)
class GitlabReconciler : Reconciler<Gitlab>, EventSourceInitializer<Gitlab> {
    override fun reconcile(resource: Gitlab, context: Context<Gitlab>) = with(context) {
        log.info("Reconciling ${resource.metadata.name}@${resource.metadata.namespace}")
        resource.patchOrUpdateStatus(
            GitlabStatus(
                getSecondaryResource<Deployment>().map { it.status?.readyReplicas ?: 0 }.orElse(0),
                getSecondaryResource<PostgresCluster>().map { it.status?.readyInstances?.let { it > 0 } }.orElse(false)
            )
        )
    }

    override fun prepareEventSources(context: EventSourceContext<Gitlab>) = with(context) {
        mutableMapOf(
            SERVICE_EVENT_SOURCE to informerEventSource<Service>()
        )
    }

    companion object {
        const val SELECTOR =
            "${Labels.MANAGED_BY_GLASSKUBE}, ${Labels.PART_OF}=${Gitlab.APP_NAME},${Labels.NAME}=${Gitlab.APP_NAME}"

        internal const val SERVICE_EVENT_SOURCE = "GiteaServiceEventSource"

        @JvmStatic
        private val log = logger()
    }
}
