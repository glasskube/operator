package eu.glasskube.operator.infra.postgres

import io.fabric8.kubernetes.api.model.ConfigMapKeySelector
import io.fabric8.kubernetes.api.model.SecretKeySelector

data class MonitoringConfiguration(
    val disableDefaultQueries: Boolean? = null,
    val customQueriesConfigMap: List<ConfigMapKeySelector>? = null,
    val customQueriesSecret: List<SecretKeySelector>? = null,
    val enablePodMonitor: Boolean? = null
)
