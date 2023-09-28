package eu.glasskube.operator.generic.dependent.postgres.backup

import io.fabric8.kubernetes.api.model.HasMetadata
import io.javaoperatorsdk.operator.api.reconciler.Context

class ChainingBackupConfigurationProvider<P : HasMetadata>(
    private vararg val providers: PostgresBackupConfigurationProvider<P>
) : PostgresBackupConfigurationProvider<P> {
    override fun getBackupConfiguration(primary: P, context: Context<P>) =
        providers.asSequence()
            .mapNotNull { it.getBackupConfiguration(primary, context) }
            .firstOrNull()
}
