package eu.glasskube.operator.postgres

data class ReplicaClusterConfiguration(
    val enabled: Boolean,
    val source: String
)
