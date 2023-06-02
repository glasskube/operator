package eu.glasskube.operator.infra.postgres

import com.fasterxml.jackson.annotation.JsonInclude
import io.fabric8.kubernetes.api.model.LocalObjectReference

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ScheduledBackupSpec(
    val schedule: String,
    val backupOwnerReference: BackupOwnerReference? = null,
    val cluster: LocalObjectReference,
    val suspend: Boolean? = null,
    val immediate: Boolean? = null
)
