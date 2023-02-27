package eu.glasskube.operator.gitea.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.gitea.Gitea
import eu.glasskube.operator.gitea.GiteaReconciler
import eu.glasskube.operator.gitea.dbClusterName
import eu.glasskube.operator.gitea.resourceLabels
import eu.glasskube.operator.postgres.BootstrapConfiguration
import eu.glasskube.operator.postgres.BootstrapInitDB
import eu.glasskube.operator.postgres.ClusterSpec
import eu.glasskube.operator.postgres.MonitoringConfiguration
import eu.glasskube.operator.postgres.PostgresCluster
import eu.glasskube.operator.postgres.StorageConfiguration
import eu.glasskube.operator.postgres.postgresCluster
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = GiteaReconciler.SELECTOR)
class GiteaPostgresCluster : CRUDKubernetesDependentResource<PostgresCluster, Gitea>(PostgresCluster::class.java) {

    override fun desired(primary: Gitea, context: Context<Gitea>) = postgresCluster {
        metadata {
            name = primary.dbClusterName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        spec = ClusterSpec(
            enableSuperuserAccess = false,
            instances = 1,
            bootstrap = BootstrapConfiguration(
                initdb = BootstrapInitDB(
                    database = "gitea"
                )
            ),
            storage = StorageConfiguration(
                size = "20Gi"
            ),
            monitoring = MonitoringConfiguration(
                enablePodMonitor = true
            )
        )
    }
}
