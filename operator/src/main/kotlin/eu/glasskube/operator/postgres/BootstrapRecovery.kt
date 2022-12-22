package eu.glasskube.operator.postgres

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.fabric8.kubernetes.api.model.LocalObjectReference

data class BootstrapRecovery @JsonCreator constructor(
    @JsonProperty("database")
    val database: String,
    @JsonProperty("owner")
    val owner: String,
    @JsonProperty("backup")
    val backup: BackupSource? = null,
    @JsonProperty("source")
    val source: String? = null,
    @JsonProperty("recoveryTarget")
    val recoveryTarget: RecoveryTarget? = null,
    @JsonProperty("secret")
    val secret: LocalObjectReference? = null
)
