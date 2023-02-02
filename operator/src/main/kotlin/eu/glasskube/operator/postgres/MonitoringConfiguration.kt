package eu.glasskube.operator.postgres

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.fabric8.kubernetes.api.model.ConfigMapKeySelector
import io.fabric8.kubernetes.api.model.SecretKeySelector

data class MonitoringConfiguration @JsonCreator constructor(
    @JsonProperty("disableDefaultQueries")
    val disableDefaultQueries: Boolean? = null,
    @JsonProperty("customQueriesConfigMap")
    val customQueriesConfigMap: List<ConfigMapKeySelector>? = null,
    @JsonProperty("customQueriesSecret")
    val customQueriesSecret: List<SecretKeySelector>? = null,
    @JsonProperty("enablePodMonitor")
    val enablePodMonitor: Boolean? = null
)
