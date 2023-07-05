package eu.glasskube.operator.generic.dependent.postgres

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.operator.infra.postgres.ScheduledBackup
import eu.glasskube.operator.infra.postgres.ScheduledBackupSpec
import eu.glasskube.operator.infra.postgres.scheduledBackup
import io.fabric8.kubernetes.api.model.HasMetadata
import io.fabric8.kubernetes.api.model.LocalObjectReference
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource

abstract class DependentPostgresScheduledBackup<P : HasMetadata>(
    override val postgresNameMapper: PostgresNameMapper<P>
) : PostgresDependentResource<P>, CRUDKubernetesDependentResource<ScheduledBackup, P>(ScheduledBackup::class.java) {

    override fun desired(primary: P, context: Context<P>) = scheduledBackup {
        metadata {
            name = postgresNameMapper.getName(primary)
            namespace = primary.namespace
            labels = postgresNameMapper.getLabels(primary)
        }
        spec = ScheduledBackupSpec(
            schedule = "0 0 3 * * *", // every day at 3:00
            cluster = LocalObjectReference(postgresNameMapper.getName(primary))
        )
    }
}
