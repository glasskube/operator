package eu.glasskube.operator.infra.prometheus.servicemonitor

import io.fabric8.kubernetes.api.model.SecretKeySelector

data class EndpointSpec(
    val port: String,
    val path: String,
    val interval: String,
    val bearerTokenSecret: SecretKeySelector? = null
)
