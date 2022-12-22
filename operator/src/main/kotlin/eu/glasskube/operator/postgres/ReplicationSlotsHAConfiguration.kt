package eu.glasskube.operator.postgres

data class ReplicationSlotsHAConfiguration(
    var enabled: Boolean,
    var slotPrefix: String? = null
)
