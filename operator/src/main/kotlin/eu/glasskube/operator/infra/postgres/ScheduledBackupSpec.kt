package eu.glasskube.operator.infra.postgres

import io.fabric8.kubernetes.api.model.LocalObjectReference

data class ScheduledBackupSpec(
    val schedule: String,
    val backupOwnerReference: BackupOwnerReference? = null,
    val cluster: LocalObjectReference,
    val suspend: Boolean? = null,
    val immediate: Boolean? = null
)
