package eu.glasskube.operator.generic.dependent.postgres.backup

import eu.glasskube.operator.apps.common.backups.database.PostgresBackupsSpec
import eu.glasskube.operator.apps.common.backups.database.ResourceWithDatabaseBackupsSpec
import eu.glasskube.operator.infra.postgres.BackupConfiguration
import eu.glasskube.operator.infra.postgres.BarmanObjectStoreConfiguration
import eu.glasskube.operator.infra.postgres.CompressionType
import eu.glasskube.operator.infra.postgres.DataBackupConfiguration
import eu.glasskube.operator.infra.postgres.S3Credentials
import eu.glasskube.operator.infra.postgres.WalBackupConfiguration
import io.fabric8.kubernetes.api.model.HasMetadata
import io.javaoperatorsdk.operator.api.reconciler.Context

open class BackupSpecBackupConfigurationProvider<P>(
    private val defaultRetentionPolicyProvider: DefaultRetentionPolicyProvider<P>
) : OptionalPostgresBackupConfigurationProvider<P>
    where P : HasMetadata, P : ResourceWithDatabaseBackupsSpec<PostgresBackupsSpec> {

    override fun getBackupConfiguration(primary: P, context: Context<P>) =
        primary.getSpec().backups?.database?.let { spec ->
            BackupConfiguration(
                BarmanObjectStoreConfiguration(
                    endpointURL = spec.s3.endpoint,
                    destinationPath = "s3://${spec.s3.bucket}",
                    s3Credentials = S3Credentials(
                        spec.s3.accessKeySecret,
                        spec.s3.secretKeySecret,
                        spec.s3.regionSecret
                    ),
                    wal = WalBackupConfiguration(CompressionType.GZIP),
                    data = DataBackupConfiguration(CompressionType.GZIP)
                ),
                retentionPolicy = spec.retentionPolicy
                    ?: defaultRetentionPolicyProvider.run { primary.getDefaultRetentionPolicy() }
            )
        }
}
