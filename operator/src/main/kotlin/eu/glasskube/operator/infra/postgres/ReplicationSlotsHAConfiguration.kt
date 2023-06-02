package eu.glasskube.operator.infra.postgres

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ReplicationSlotsHAConfiguration(
    val enabled: Boolean,
    val slotPrefix: String? = null
)
