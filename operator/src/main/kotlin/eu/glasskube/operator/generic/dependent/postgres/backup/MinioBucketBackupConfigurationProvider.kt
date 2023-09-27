package eu.glasskube.operator.generic.dependent.postgres.backup

import eu.glasskube.kubernetes.api.model.secretKeySelector
import eu.glasskube.operator.Environment
import eu.glasskube.operator.generic.dependent.postgres.backup.bucketinfo.MinioBucketInfoProvider
import eu.glasskube.operator.infra.minio.MinioBucket
import eu.glasskube.operator.infra.postgres.BackupConfiguration
import eu.glasskube.operator.infra.postgres.BarmanObjectStoreConfiguration
import eu.glasskube.operator.infra.postgres.CompressionType
import eu.glasskube.operator.infra.postgres.DataBackupConfiguration
import eu.glasskube.operator.infra.postgres.S3Credentials
import eu.glasskube.operator.infra.postgres.WalBackupConfiguration
import io.fabric8.kubernetes.api.model.HasMetadata
import io.javaoperatorsdk.operator.api.reconciler.Context

open class MinioBucketBackupConfigurationProvider<P : HasMetadata>(
    private val bucketInfoProvider: MinioBucketInfoProvider<P>,
    private val defaultRetentionPolicyProvider: DefaultRetentionPolicyProvider<P>
) : PostgresBackupConfigurationProvider<P> {
    override fun getBackupConfiguration(primary: P, context: Context<P>) =
        bucketInfoProvider.getMinioBucketInfo(primary, context).let { bucketInfo ->
            BackupConfiguration(
                BarmanObjectStoreConfiguration(
                    endpointURL = "http://${Environment.MINIO_HOST_NAME}.${Environment.NAMESPACE}:9000",
                    destinationPath = "s3://${bucketInfo.bucketName}",
                    s3Credentials = S3Credentials(
                        secretKeySelector(bucketInfo.secretName, MinioBucket.USERNAME_KEY),
                        secretKeySelector(bucketInfo.secretName, MinioBucket.PASSWORD_KEY)
                    ),
                    wal = WalBackupConfiguration(CompressionType.GZIP),
                    data = DataBackupConfiguration(CompressionType.GZIP)
                ),
                retentionPolicy = defaultRetentionPolicyProvider.run { primary.getDefaultRetentionPolicy() }
            )
        }
}
