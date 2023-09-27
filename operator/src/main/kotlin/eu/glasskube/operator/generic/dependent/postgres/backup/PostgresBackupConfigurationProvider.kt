package eu.glasskube.operator.generic.dependent.postgres.backup

import eu.glasskube.operator.infra.postgres.BackupConfiguration
import io.fabric8.kubernetes.api.model.HasMetadata
import io.javaoperatorsdk.operator.api.reconciler.Context

fun interface PostgresBackupConfigurationProvider<P : HasMetadata> : OptionalPostgresBackupConfigurationProvider<P> {
    override fun getBackupConfiguration(primary: P, context: Context<P>): BackupConfiguration
}
