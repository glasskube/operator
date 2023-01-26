package eu.glasskube.operator.postgres

import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Version

@Group("postgresql.cnpg.io")
@Version("v1")
class ScheduledBackup : CustomResource<ScheduledBackupSpec, ScheduledBackupStatus>(), Namespaced {
    override fun setSpec(spec: ScheduledBackupSpec?) {
        super.setSpec(spec)
    }
}

inline fun scheduledBackup(block: ScheduledBackup.() -> Unit): ScheduledBackup =
    ScheduledBackup().apply(block)
