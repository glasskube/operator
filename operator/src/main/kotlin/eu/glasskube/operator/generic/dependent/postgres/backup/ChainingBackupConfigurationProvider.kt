package eu.glasskube.operator.generic.dependent.postgres.backup

import io.fabric8.kubernetes.api.model.HasMetadata
import io.javaoperatorsdk.operator.api.reconciler.Context

class ChainingBackupConfigurationProvider<P : HasMetadata>(
    private val fallbackProvider: PostgresBackupConfigurationProvider<P>,
    private vararg val optionalProviders: OptionalPostgresBackupConfigurationProvider<P>
) : PostgresBackupConfigurationProvider<P> {
    override fun getBackupConfiguration(primary: P, context: Context<P>) =
        optionalProviders.reversed().asSequence()
            .mapNotNull { it.getBackupConfiguration(primary, context) }
            .firstOrNull()
            ?: fallbackProvider.getBackupConfiguration(primary, context)
}
