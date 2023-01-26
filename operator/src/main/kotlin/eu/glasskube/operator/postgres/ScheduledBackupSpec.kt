package eu.glasskube.operator.postgres

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.fabric8.kubernetes.api.model.LocalObjectReference

data class ScheduledBackupSpec @JsonCreator constructor(
    @JsonProperty("schedule")
    val schedule: String,
    @JsonProperty("backupOwnerReference")
    val backupOwnerReference: BackupOwnerReference? = null,
    @JsonProperty("cluster")
    val cluster: LocalObjectReference,
    @JsonProperty("suspend")
    val suspend: Boolean? = null,
    @JsonProperty("immediate")
    val immediate: Boolean? = null,
)
