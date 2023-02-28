package eu.glasskube.operator.prometheus.servicemonitor

import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Kind
import io.fabric8.kubernetes.model.annotation.Version

@Kind("ServiceMonitor")
@Group("monitoring.coreos.com")
@Version("v1")
class ServiceMonitor : CustomResource<ServiceMonitorSpec, Any>(), Namespaced

inline fun serviceMonitor(block: ServiceMonitor.() -> Unit) =
    ServiceMonitor().apply(block)
