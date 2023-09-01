package eu.glasskube.operator.generic.dependent.postgres

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.kubernetes.api.model.secretKeySelector
import eu.glasskube.operator.config.ConfigService
import eu.glasskube.operator.infra.minio.MinioBucket
import eu.glasskube.operator.infra.postgres.BackupConfiguration
import eu.glasskube.operator.infra.postgres.BarmanObjectStoreConfiguration
import eu.glasskube.operator.infra.postgres.BootstrapConfiguration
import eu.glasskube.operator.infra.postgres.BootstrapInitDB
import eu.glasskube.operator.infra.postgres.ClusterSpec
import eu.glasskube.operator.infra.postgres.CompressionType
import eu.glasskube.operator.infra.postgres.DataBackupConfiguration
import eu.glasskube.operator.infra.postgres.EmbeddedObjectMetadata
import eu.glasskube.operator.infra.postgres.MonitoringConfiguration
import eu.glasskube.operator.infra.postgres.PostgresCluster
import eu.glasskube.operator.infra.postgres.S3Credentials
import eu.glasskube.operator.infra.postgres.StorageConfiguration
import eu.glasskube.operator.infra.postgres.WalBackupConfiguration
import eu.glasskube.operator.infra.postgres.postgresCluster
import io.fabric8.kubernetes.api.model.HasMetadata
import io.fabric8.kubernetes.api.model.Quantity
import io.fabric8.kubernetes.api.model.ResourceRequirements
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource

abstract class DependentPostgresCluster<P : HasMetadata>(
    override val postgresNameMapper: PostgresNameMapper<P>,
    private val configService: ConfigService
) : PostgresDependentResource<P>, CRUDKubernetesDependentResource<PostgresCluster, P>(PostgresCluster::class.java) {

    protected abstract val P.storageSize: String
    protected open val P.storageClass: String? get() = null
    protected open val P.databaseOwnerName: String? get() = null
    protected open val P.initSql: String? get() = null
    protected open val P.databaseResources: ResourceRequirements
        get() = ResourceRequirements(
            null,
            mapOf("memory" to Quantity("512", "Mi")),
            mapOf("memory" to Quantity("256", "Mi"))
        )
    protected open val backupInfoProvider: PostgresBackupInfoProvider<P> =
        MinioBucketBackupInfoProvider.Default()
    protected open val P.backupRetentionPolicy: String? get() = null

    override fun desired(primary: P, context: Context<P>) = postgresCluster {
        val backupInfo = backupInfoProvider.getBackupInfo(primary, context)

        metadata {
            name = postgresNameMapper.getName(primary)
            namespace = primary.namespace
            labels = postgresNameMapper.getLabels(primary)
        }
        spec = ClusterSpec(
            instances = 1,
            enableSuperuserAccess = false,
            inheritedMetadata = EmbeddedObjectMetadata(
                annotations = configService.getBackupAnnotations("pgdata")
            ),
            bootstrap = BootstrapConfiguration(
                initdb = BootstrapInitDB(
                    database = postgresNameMapper.getDatabaseName(primary),
                    owner = primary.databaseOwnerName,
                    postInitApplicationSQL = primary.initSql?.let { listOf(it) }
                )
            ),
            storage = StorageConfiguration(storageClass = primary.storageClass, size = primary.storageSize),
            backup = BackupConfiguration(
                BarmanObjectStoreConfiguration(
                    endpointURL = "http://glasskube-minio.glasskube-system:9000",
                    destinationPath = "s3://${backupInfo.bucketName}",
                    s3Credentials = S3Credentials(
                        secretKeySelector(backupInfo.secretName, MinioBucket.USERNAME_KEY),
                        secretKeySelector(backupInfo.secretName, MinioBucket.PASSWORD_KEY)
                    ),
                    wal = WalBackupConfiguration(CompressionType.GZIP),
                    data = DataBackupConfiguration(CompressionType.GZIP)
                ),
                retentionPolicy = primary.backupRetentionPolicy
            ),
            monitoring = MonitoringConfiguration(enablePodMonitor = true),
            resources = primary.databaseResources
        )
    }
}
