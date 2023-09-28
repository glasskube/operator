package eu.glasskube.operator.generic.dependent.postgres.backup

import eu.glasskube.operator.apps.common.database.ResourceWithDatabaseSpec
import eu.glasskube.operator.apps.common.database.postgres.PostgresDatabaseSpec
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
    where P : HasMetadata, P : ResourceWithDatabaseSpec<PostgresDatabaseSpec> {

    override fun getBackupConfiguration(primary: P, context: Context<P>) =
        primary.getSpec().database?.backups?.let { spec ->
            BackupConfiguration(
                BarmanObjectStoreConfiguration(
                    endpointURL = spec.s3.endpoint?.let {
                        when {
                            "://" in it -> it
                            else -> "https://$it"
                        }
                    },
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
