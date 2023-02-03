package eu.glasskube.operator.mariadb

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class ServiceMonitor @JsonCreator constructor(
    @JsonProperty("prometheusRelease")
    val prometheusRelease: String,
    @JsonProperty("interval")
    val interval: String? = "10s",
    @JsonProperty("scrapeTimeout")
    val scrapeTimeout: String? = "10s"
)
