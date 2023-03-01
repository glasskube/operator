package eu.glasskube.operator.prometheus.servicemonitor

import io.fabric8.kubernetes.api.model.LabelSelector

data class ServiceMonitorSpec(
    val endpoints: List<EndpointSpec>,
    val selector: LabelSelector
)
