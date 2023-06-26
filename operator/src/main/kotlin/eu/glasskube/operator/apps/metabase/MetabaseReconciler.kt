package eu.glasskube.operator.apps.metabase

import eu.glasskube.kubernetes.client.patchOrUpdateStatus
import eu.glasskube.operator.Labels
import eu.glasskube.operator.api.reconciler.getSecondaryResource
import eu.glasskube.operator.apps.metabase.dependent.MetabaseConfigMap
import eu.glasskube.operator.apps.metabase.dependent.MetabaseDeployment
import eu.glasskube.operator.apps.metabase.dependent.MetabaseHttpService
import eu.glasskube.operator.apps.metabase.dependent.MetabaseIngress
import eu.glasskube.operator.apps.metabase.dependent.MetabaseMinioBucket
import eu.glasskube.operator.apps.metabase.dependent.MetabasePostgresBackup
import eu.glasskube.operator.apps.metabase.dependent.MetabasePostgresCluster
import eu.glasskube.operator.apps.metabase.dependent.MetabaseSecret
import eu.glasskube.operator.apps.metabase.dependent.MetabaseServiceMonitor
import eu.glasskube.operator.infra.postgres.PostgresCluster
import eu.glasskube.operator.logger
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration
import io.javaoperatorsdk.operator.api.reconciler.Reconciler
import io.javaoperatorsdk.operator.api.reconciler.dependent.Dependent

@ControllerConfiguration(
    dependents = [
        Dependent(type = MetabaseSecret::class, name = "MetabaseSecret"),
        Dependent(type = MetabaseMinioBucket::class, name = "MetabaseMinioBucket"),
        Dependent(
            type = MetabasePostgresCluster::class,
            name = "MetabasePostgresCluster",
            readyPostcondition = MetabasePostgresCluster.ReadyPostCondition::class,
            dependsOn = ["MetabaseMinioBucket"]
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
        )
    ]
)
class MetabaseReconciler : Reconciler<Metabase> {

    override fun reconcile(resource: Metabase, context: Context<Metabase>) = with(context) {
        log.info("Reconciling ${resource.metadata.name}@${resource.metadata.namespace}")
        resource.patchOrUpdateStatus(
            MetabaseStatus(
                getSecondaryResource<Deployment>().map { it.status?.readyReplicas ?: 0 }.orElse(0),
                getSecondaryResource<PostgresCluster>().map { it.status?.readyInstances?.let { it > 0 } }.orElse(false)
            )
        )
    }

    companion object {
        const val SELECTOR =
            "${Labels.MANAGED_BY_GLASSKUBE},${Labels.PART_OF}=${Metabase.APP_NAME},${Labels.NAME}=${Metabase.APP_NAME}"

        @JvmStatic
        private val log = logger()
    }
}
