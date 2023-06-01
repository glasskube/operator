package eu.glasskube.operator.infra.postgres

data class SyncReplicaElectionConstraints(
    val enabled: Boolean? = null,
    val nodeLabelsAntiAffinity: List<String>? = null
)
