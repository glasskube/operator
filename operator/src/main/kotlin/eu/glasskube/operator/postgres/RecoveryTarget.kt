package eu.glasskube.operator.postgres

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class RecoveryTarget @JsonCreator constructor(
    @JsonProperty("backupID")
    val backupID: String? = null,
    @JsonProperty("targetTLI")
    val targetTLI: String? = null,
    @JsonProperty("targetXID")
    val targetXID: String? = null,
    @JsonProperty("targetName")
    val targetName: String? = null,
    @JsonProperty("targetLSN")
    val targetLSN: String? = null,
    @JsonProperty("targetTime")
    val targetTime: String? = null,
    @JsonProperty("targetImmediate")
    val targetImmediate: Boolean? = null,
    @JsonProperty("exclusive")
    val exclusive: Boolean? = null
)
