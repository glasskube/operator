package eu.glasskube.operator.infra.postgres

data class ReplicationSlotsConfiguration(
    val highAvailability: ReplicationSlotsHAConfiguration? = null,
    val updateInterval: Int? = null
)
