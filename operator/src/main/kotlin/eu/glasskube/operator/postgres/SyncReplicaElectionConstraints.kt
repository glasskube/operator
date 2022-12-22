package eu.glasskube.operator.postgres

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class SyncReplicaElectionConstraints @JsonCreator constructor(
    @JsonProperty("enabled")
    val enabled: Boolean? = null,
    @JsonProperty("nodeLabelsAntiAffinity")
    val nodeLabelsAntiAffinity: List<String>? = null
)
