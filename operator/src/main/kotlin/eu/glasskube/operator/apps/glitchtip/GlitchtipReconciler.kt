package eu.glasskube.operator.apps.glitchtip

import eu.glasskube.kubernetes.client.patchOrUpdateStatus
import eu.glasskube.operator.Labels
import eu.glasskube.operator.api.reconciler.getSecondaryResource
import eu.glasskube.operator.apps.glitchtip.dependent.GlitchtipConfigMap
import eu.glasskube.operator.apps.glitchtip.dependent.GlitchtipDeployment
import eu.glasskube.operator.apps.glitchtip.dependent.GlitchtipHttpService
import eu.glasskube.operator.apps.glitchtip.dependent.GlitchtipIngress
import eu.glasskube.operator.apps.glitchtip.dependent.GlitchtipMinioBucket
import eu.glasskube.operator.apps.glitchtip.dependent.GlitchtipPostgresBackup
import eu.glasskube.operator.apps.glitchtip.dependent.GlitchtipPostgresCluster
import eu.glasskube.operator.apps.glitchtip.dependent.GlitchtipSecret
import eu.glasskube.operator.infra.postgres.PostgresCluster
import eu.glasskube.operator.logger
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration
import io.javaoperatorsdk.operator.api.reconciler.Reconciler
import io.javaoperatorsdk.operator.api.reconciler.dependent.Dependent

@ControllerConfiguration(
    dependents = [
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
            type = GlitchtipDeployment::class,
            name = "GlitchtipDeployment",
            dependsOn = ["GlitchtipPostgresCluster", "GlitchtipConfigMap", "GlitchtipSecret"]
        ),
        Dependent(
            type = GlitchtipHttpService::class,
            name = "GlitchtipHttpService",
            dependsOn = ["GlitchtipDeployment"]
        ),
        Dependent(
            type = GlitchtipIngress::class,
            name = "GlitchtipIngress",
            dependsOn = ["GlitchtipHttpService"]
        )
    ]
)
class GlitchtipReconciler : Reconciler<Glitchtip> {

    override fun reconcile(resource: Glitchtip, context: Context<Glitchtip>) = with(context) {
        log.info("Reconciling ${resource.metadata.name}@${resource.metadata.namespace}")
        resource.patchOrUpdateStatus(
            GlitchtipStatus(
                getSecondaryResource<Deployment>().map { it.status?.readyReplicas ?: 0 }.orElse(0),
                getSecondaryResource<PostgresCluster>().map { it.status?.readyInstances?.let { it > 0 } }.orElse(false)
            )
        )
    }

    companion object {
        const val SELECTOR =
            "${Labels.MANAGED_BY_GLASSKUBE},${Labels.PART_OF}=${Glitchtip.APP_NAME},${Labels.NAME}=${Glitchtip.APP_NAME}"

        @JvmStatic
        private val log = logger()
    }
}
