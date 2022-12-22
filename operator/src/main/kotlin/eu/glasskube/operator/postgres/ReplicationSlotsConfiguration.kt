package eu.glasskube.operator.postgres

data class ReplicationSlotsConfiguration(
    var highAvailability: ReplicationSlotsHAConfiguration? = null,
    var updateInterval: Int? = null
)
