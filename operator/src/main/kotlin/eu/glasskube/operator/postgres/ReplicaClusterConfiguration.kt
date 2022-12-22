package eu.glasskube.operator.postgres

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class ReplicaClusterConfiguration @JsonCreator constructor(
    @JsonProperty("enabled")
    val enabled: Boolean,
    @JsonProperty("source")
    val source: String
)
