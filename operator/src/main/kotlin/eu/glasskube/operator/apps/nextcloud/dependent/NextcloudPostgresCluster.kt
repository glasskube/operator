package eu.glasskube.operator.apps.nextcloud.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.kubernetes.api.model.secretKeySelector
import eu.glasskube.operator.api.reconciler.requireSecondaryResource
import eu.glasskube.operator.apps.nextcloud.Nextcloud
import eu.glasskube.operator.apps.nextcloud.NextcloudReconciler
import eu.glasskube.operator.apps.nextcloud.databaseClusterName
import eu.glasskube.operator.apps.nextcloud.databaseName
import eu.glasskube.operator.apps.nextcloud.resourceLabels
import eu.glasskube.operator.generic.condition.PostgresReadyCondition
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
import io.fabric8.kubernetes.api.model.Quantity
import io.fabric8.kubernetes.api.model.ResourceRequirements
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = NextcloudReconciler.SELECTOR)
class NextcloudPostgresCluster :
    CRUDKubernetesDependentResource<PostgresCluster, Nextcloud>(PostgresCluster::class.java) {

    class ReadyPostCondition : PostgresReadyCondition<Nextcloud>()

    override fun desired(primary: Nextcloud, context: Context<Nextcloud>) = postgresCluster {
        val minioBucket: MinioBucket by context.requireSecondaryResource()

        metadata {
            name = primary.databaseClusterName
            namespace = primary.namespace
            labels = primary.resourceLabels
        }
        spec = ClusterSpec(
            instances = 1,
            enableSuperuserAccess = false,
            bootstrap = BootstrapConfiguration(
                initdb = BootstrapInitDB(
                    database = primary.databaseName
                )
            ),
            storage = StorageConfiguration(
                size = "10Gi"
            ),
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
            ),
            resources = ResourceRequirements(
                null,
                mapOf("memory" to Quantity("512", "Mi")),
                mapOf("memory" to Quantity("256", "Mi"))
            )
        )
    }
}
