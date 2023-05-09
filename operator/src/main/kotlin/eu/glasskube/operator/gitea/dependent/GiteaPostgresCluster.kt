package eu.glasskube.operator.gitea.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.secretKeySelector
import eu.glasskube.operator.api.reconciler.requireSecondaryResource
import eu.glasskube.operator.gitea.Gitea
import eu.glasskube.operator.gitea.GiteaReconciler
import eu.glasskube.operator.gitea.dbClusterName
import eu.glasskube.operator.gitea.resourceLabels
import eu.glasskube.operator.minio.MinioBucket
import eu.glasskube.operator.minio.bucketName
import eu.glasskube.operator.minio.secretName
import eu.glasskube.operator.postgres.BackupConfiguration
import eu.glasskube.operator.postgres.BarmanObjectStoreConfiguration
import eu.glasskube.operator.postgres.BootstrapConfiguration
import eu.glasskube.operator.postgres.BootstrapInitDB
import eu.glasskube.operator.postgres.ClusterSpec
import eu.glasskube.operator.postgres.CompressionType
import eu.glasskube.operator.postgres.DataBackupConfiguration
import eu.glasskube.operator.postgres.MonitoringConfiguration
import eu.glasskube.operator.postgres.PostgresCluster
import eu.glasskube.operator.postgres.S3Credentials
import eu.glasskube.operator.postgres.StorageConfiguration
import eu.glasskube.operator.postgres.WalBackupConfiguration
import eu.glasskube.operator.postgres.postgresCluster
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = GiteaReconciler.SELECTOR)
class GiteaPostgresCluster : CRUDKubernetesDependentResource<PostgresCluster, Gitea>(PostgresCluster::class.java) {

    override fun desired(primary: Gitea, context: Context<Gitea>) = postgresCluster {
        val minioBucket: MinioBucket by context.requireSecondaryResource()

        metadata {
            name = primary.dbClusterName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }

        spec = ClusterSpec(
            enableSuperuserAccess = false,
            instances = 1,
            bootstrap = BootstrapConfiguration(
                initdb = BootstrapInitDB(database = "gitea")
            ),
            storage = StorageConfiguration(size = "20Gi"),
            monitoring = MonitoringConfiguration(enablePodMonitor = true),
            backup = BackupConfiguration(
                barmanObjectStore =
                BarmanObjectStoreConfiguration(
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
