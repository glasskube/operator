package eu.glasskube.operator.infra.postgres

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ReplicationSlotsConfiguration(
    val highAvailability: ReplicationSlotsHAConfiguration? = null,
    val updateInterval: Int? = null
)
