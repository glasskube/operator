package eu.glasskube.operator.postgres

data class ReplicaClusterConfiguration(
    var enabled: Boolean,
    var source: String
)
