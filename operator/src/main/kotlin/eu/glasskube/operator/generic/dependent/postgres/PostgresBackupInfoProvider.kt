package eu.glasskube.operator.generic.dependent.postgres

import io.fabric8.kubernetes.api.model.HasMetadata
import io.javaoperatorsdk.operator.api.reconciler.Context

fun interface PostgresBackupInfoProvider<P : HasMetadata> {
    fun getBackupInfo(primary: P, context: Context<P>): PostgresBackupInfo
}
