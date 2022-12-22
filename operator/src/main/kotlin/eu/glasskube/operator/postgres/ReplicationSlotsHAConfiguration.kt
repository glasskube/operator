package eu.glasskube.operator.postgres

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class ReplicationSlotsHAConfiguration @JsonCreator constructor(
    @JsonProperty("enabled")
    val enabled: Boolean,
    @JsonProperty("slotPrefix")
    val slotPrefix: String? = null
)
