package eu.glasskube.operator.generic.dependent.postgres

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.operator.apps.common.database.ResourceWithDatabaseSpec
import eu.glasskube.operator.apps.common.database.postgres.PostgresDatabaseSpec
import eu.glasskube.operator.infra.postgres.ScheduledBackup
import eu.glasskube.operator.infra.postgres.ScheduledBackupSpec
import eu.glasskube.operator.infra.postgres.scheduledBackup
import io.fabric8.kubernetes.api.model.HasMetadata
import io.fabric8.kubernetes.api.model.LocalObjectReference
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource

abstract class DependentPostgresScheduledBackup<P>(
    override val postgresNameMapper: PostgresNameMapper<P>
) : PostgresDependentResource<P>, CRUDKubernetesDependentResource<ScheduledBackup, P>(ScheduledBackup::class.java)
    where P : HasMetadata, P : ResourceWithDatabaseSpec<PostgresDatabaseSpec> {

    /**
     * The schedule that should be declared for this scheduled backup if not otherwise specified in the backup spec.
     *
     * Unless overridden, this default is: every day at 3:00
     */
    protected open val P.defaultSchedule get() = "0 0 3 * * *"

    override fun desired(primary: P, context: Context<P>) = scheduledBackup {
        metadata {
            name = postgresNameMapper.getName(primary)
            namespace = primary.namespace
            labels = postgresNameMapper.getLabels(primary)
        }
        spec = ScheduledBackupSpec(
            schedule = primary.getSpec().database.backups?.schedule ?: primary.defaultSchedule,
            cluster = LocalObjectReference(postgresNameMapper.getName(primary))
        )
    }
}
