package eu.glasskube.operator.mariadb

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class Metrics @JsonCreator constructor(
    @JsonProperty("exporter")
    val exporter: Exporter,
    @JsonProperty("serviceMonitor")
    val serviceMonitor: ServiceMonitor
)
