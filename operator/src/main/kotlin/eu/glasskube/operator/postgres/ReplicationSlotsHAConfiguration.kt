package eu.glasskube.operator.postgres

data class ReplicationSlotsHAConfiguration(
    val enabled: Boolean,
    val slotPrefix: String? = null
)
