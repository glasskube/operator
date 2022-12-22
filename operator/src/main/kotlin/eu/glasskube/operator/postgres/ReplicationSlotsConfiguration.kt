package eu.glasskube.operator.postgres

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class ReplicationSlotsConfiguration @JsonCreator constructor(
    @JsonProperty("highAvailability")
    val highAvailability: ReplicationSlotsHAConfiguration? = null,
    @JsonProperty("updateInterval")
    val updateInterval: Int? = null
)
