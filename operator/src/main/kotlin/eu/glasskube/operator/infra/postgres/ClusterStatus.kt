package eu.glasskube.operator.infra.postgres

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ClusterStatus(
    val instances: Int
)
