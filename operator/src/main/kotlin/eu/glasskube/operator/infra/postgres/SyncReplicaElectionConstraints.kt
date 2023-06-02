package eu.glasskube.operator.infra.postgres

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SyncReplicaElectionConstraints(
    val enabled: Boolean? = null,
    val nodeLabelsAntiAffinity: List<String>? = null
)
