package eu.glasskube.operator.postgres

data class SyncReplicaElectionConstraints(
    var enabled: Boolean? = null,
    var nodeLabelsAntiAffinity: List<String>? = null
)
