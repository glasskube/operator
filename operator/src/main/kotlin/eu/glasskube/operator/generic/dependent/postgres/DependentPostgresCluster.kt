package eu.glasskube.operator.generic.dependent.postgres

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.operator.apps.common.database.ResourceWithDatabaseSpec
import eu.glasskube.operator.apps.common.database.postgres.PostgresDatabaseSpec
import eu.glasskube.operator.config.ConfigService
import eu.glasskube.operator.generic.dependent.postgres.backup.BackupSpecBackupConfigurationProvider
import eu.glasskube.operator.generic.dependent.postgres.backup.ChainingBackupConfigurationProvider
import eu.glasskube.operator.generic.dependent.postgres.backup.MinioBucketBackupConfigurationProvider
import eu.glasskube.operator.generic.dependent.postgres.backup.PostgresBackupConfigurationProvider
import eu.glasskube.operator.generic.dependent.postgres.backup.bucketinfo.MinioBucketInfoProvider
import eu.glasskube.operator.generic.dependent.postgres.backup.bucketinfo.SecondaryResourceMinioBucketInfoProvider
import eu.glasskube.operator.infra.postgres.BootstrapConfiguration
import eu.glasskube.operator.infra.postgres.BootstrapInitDB
import eu.glasskube.operator.infra.postgres.ClusterSpec
import eu.glasskube.operator.infra.postgres.EmbeddedObjectMetadata
import eu.glasskube.operator.infra.postgres.MonitoringConfiguration
import eu.glasskube.operator.infra.postgres.PostgresCluster
import eu.glasskube.operator.infra.postgres.StorageConfiguration
import eu.glasskube.operator.infra.postgres.postgresCluster
import io.fabric8.kubernetes.api.model.HasMetadata
import io.fabric8.kubernetes.api.model.Quantity
import io.fabric8.kubernetes.api.model.ResourceRequirements
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource

abstract class DependentPostgresCluster<P>(
    override val postgresNameMapper: PostgresNameMapper<P>,
    private val configService: ConfigService
) : CRUDKubernetesDependentResource<PostgresCluster, P>(PostgresCluster::class.java), PostgresDependentResource<P>
    where P : HasMetadata, P : ResourceWithDatabaseSpec<PostgresDatabaseSpec> {

    protected abstract val P.defaultStorageSize: String
    protected open val P.defaultStorageClass: String? get() = null
    protected open val P.databaseOwnerName: String? get() = null
    protected open val P.initSql: String? get() = null
    protected open val P.databaseResources: ResourceRequirements
        get() = ResourceRequirements(
            null,
            mapOf("memory" to Quantity("512", "Mi")),
            mapOf("memory" to Quantity("256", "Mi"))
        )
    protected open val backupBucketInfoProvider: MinioBucketInfoProvider<P> =
        SecondaryResourceMinioBucketInfoProvider.Default()
    protected open val P.defaultBackupRetentionPolicy: String? get() = null
    protected open val backupConfigurationProvider: PostgresBackupConfigurationProvider<P> =
        ChainingBackupConfigurationProvider(
            MinioBucketBackupConfigurationProvider(
                { primary, context -> backupBucketInfoProvider.getMinioBucketInfo(primary, context) },
                { defaultBackupRetentionPolicy }
            ),
            BackupSpecBackupConfigurationProvider { defaultBackupRetentionPolicy }
        )

    override fun desired(primary: P, context: Context<P>) = postgresCluster {
        metadata {
            name = postgresNameMapper.getName(primary)
            namespace = primary.namespace
            labels = postgresNameMapper.getLabels(primary)
        }
        spec = ClusterSpec(
            instances = primary.getSpec().database.instances,
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
            storage = StorageConfiguration(
                storageClass = primary.getSpec().database.storage?.storageClass ?: primary.defaultStorageClass,
                size = primary.getSpec().database.storage?.size ?: primary.defaultStorageSize
            ),
            backup = backupConfigurationProvider.getBackupConfiguration(primary, context),
            monitoring = MonitoringConfiguration(enablePodMonitor = true),
            resources = primary.databaseResources
        )
    }
}
