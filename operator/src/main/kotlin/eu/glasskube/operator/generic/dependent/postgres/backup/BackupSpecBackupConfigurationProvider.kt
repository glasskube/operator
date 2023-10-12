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
) : PostgresBackupConfigurationProvider<P>
    where P : HasMetadata, P : ResourceWithDatabaseSpec<PostgresDatabaseSpec> {

    override fun getBackupConfiguration(primary: P, context: Context<P>) =
        primary.getSpec().database.backups?.run {
            s3?.let { s3Spec ->
                BackupConfiguration(
                    BarmanObjectStoreConfiguration(
                        endpointURL = s3Spec.endpoint?.let {
                            when {
                                "://" in it -> it
                                else -> "https://$it"
                            }
                        },
                        destinationPath = "s3://${s3Spec.bucket}",
                        s3Credentials = S3Credentials(
                            s3Spec.accessKeySecret,
                            s3Spec.secretKeySecret,
                            s3Spec.regionSecret
                        ),
                        wal = WalBackupConfiguration(CompressionType.GZIP),
                        data = DataBackupConfiguration(CompressionType.GZIP)
                    ),
                    retentionPolicy = retentionPolicy
                        ?: defaultRetentionPolicyProvider.run { primary.getDefaultRetentionPolicy() }
                )
            }
        }
}
