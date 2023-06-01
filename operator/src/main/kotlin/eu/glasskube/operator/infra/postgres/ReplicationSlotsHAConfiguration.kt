package eu.glasskube.operator.infra.postgres

data class ReplicationSlotsHAConfiguration(
    val enabled: Boolean,
    val slotPrefix: String? = null
)
