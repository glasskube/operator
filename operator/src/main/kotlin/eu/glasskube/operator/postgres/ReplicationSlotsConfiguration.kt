package eu.glasskube.operator.postgres

data class ReplicationSlotsConfiguration(
    val highAvailability: ReplicationSlotsHAConfiguration? = null,
    val updateInterval: Int? = null
)
