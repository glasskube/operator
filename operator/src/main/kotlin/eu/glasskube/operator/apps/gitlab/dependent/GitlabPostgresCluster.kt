package eu.glasskube.operator.apps.gitlab.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.secretKeySelector
import eu.glasskube.operator.api.reconciler.requireSecondaryResource
import eu.glasskube.operator.apps.gitlab.Gitlab
import eu.glasskube.operator.apps.gitlab.GitlabReconciler
import eu.glasskube.operator.apps.gitlab.databaseName
import eu.glasskube.operator.apps.gitlab.resourceLabels
import eu.glasskube.operator.infra.minio.MinioBucket
import eu.glasskube.operator.infra.minio.bucketName
import eu.glasskube.operator.infra.minio.secretName
import eu.glasskube.operator.infra.postgres.BackupConfiguration
import eu.glasskube.operator.infra.postgres.BarmanObjectStoreConfiguration
import eu.glasskube.operator.infra.postgres.BootstrapConfiguration
import eu.glasskube.operator.infra.postgres.BootstrapInitDB
import eu.glasskube.operator.infra.postgres.ClusterSpec
import eu.glasskube.operator.infra.postgres.CompressionType
import eu.glasskube.operator.infra.postgres.DataBackupConfiguration
import eu.glasskube.operator.infra.postgres.MonitoringConfiguration
import eu.glasskube.operator.infra.postgres.PostgresCluster
import eu.glasskube.operator.infra.postgres.S3Credentials
import eu.glasskube.operator.infra.postgres.StorageConfiguration
import eu.glasskube.operator.infra.postgres.WalBackupConfiguration
import eu.glasskube.operator.infra.postgres.postgresCluster
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.dependent.DependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.dependent.workflow.Condition
import kotlin.jvm.optionals.getOrDefault

@KubernetesDependent(labelSelector = GitlabReconciler.SELECTOR)
class GitlabPostgresCluster : CRUDKubernetesDependentResource<PostgresCluster, Gitlab>(PostgresCluster::class.java) {
    class ReadyPostCondition : Condition<PostgresCluster, Gitlab> {
        override fun isMet(
            dependentResource: DependentResource<PostgresCluster, Gitlab>,
            primary: Gitlab,
            context: Context<Gitlab>
        ): Boolean = dependentResource.getSecondaryResource(primary, context)
            .map { cluster -> cluster.status?.readyInstances?.let { it > 0 } }
            .getOrDefault(false)
    }

    override fun desired(primary: Gitlab, context: Context<Gitlab>) = postgresCluster {
        val minioBucket: MinioBucket by context.requireSecondaryResource()

        metadata {
            name = primary.databaseName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        spec = ClusterSpec(
            enableSuperuserAccess = false,
            instances = 1,
            bootstrap = BootstrapConfiguration(
                initdb = BootstrapInitDB(database = "gitlabhq_production", owner = "gitlab")
            ),
            storage = StorageConfiguration(size = "20Gi"),
            monitoring = MonitoringConfiguration(enablePodMonitor = true),
            backup = BackupConfiguration(
                barmanObjectStore = BarmanObjectStoreConfiguration(
                    endpointURL = "http://glasskube-minio.glasskube-system:9000",
                    destinationPath = "s3://${minioBucket.bucketName}",
                    s3Credentials = S3Credentials(
                        accessKeyId = secretKeySelector(minioBucket.secretName, MinioBucket.USERNAME_KEY),
                        secretAccessKey = secretKeySelector(minioBucket.secretName, MinioBucket.PASSWORD_KEY)
                    ),
                    wal = WalBackupConfiguration(compression = CompressionType.GZIP),
                    data = DataBackupConfiguration(compression = CompressionType.GZIP)
                )
            )
        )
    }
}
