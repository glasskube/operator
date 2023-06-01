package eu.glasskube.operator.infra.postgres

data class ReplicaClusterConfiguration(
    val enabled: Boolean,
    val source: String
)
