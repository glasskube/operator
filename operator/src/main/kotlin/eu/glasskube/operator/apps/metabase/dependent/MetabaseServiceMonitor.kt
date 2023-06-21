package eu.glasskube.operator.apps.metabase.dependent

import eu.glasskube.kubernetes.api.model.labelSelector
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.apps.metabase.Metabase
import eu.glasskube.operator.apps.metabase.MetabaseReconciler
import eu.glasskube.operator.apps.metabase.genericResourceName
import eu.glasskube.operator.apps.metabase.resourceLabelSelector
import eu.glasskube.operator.apps.metabase.resourceLabels
import eu.glasskube.operator.infra.prometheus.servicemonitor.EndpointSpec
import eu.glasskube.operator.infra.prometheus.servicemonitor.ServiceMonitor
import eu.glasskube.operator.infra.prometheus.servicemonitor.ServiceMonitorSpec
import eu.glasskube.operator.infra.prometheus.servicemonitor.serviceMonitor
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

// https://www.metabase.com/docs/latest/installation-and-operation/observability-with-prometheus
@KubernetesDependent(labelSelector = MetabaseReconciler.SELECTOR)
class MetabaseServiceMonitor : CRUDKubernetesDependentResource<ServiceMonitor, Metabase>(ServiceMonitor::class.java) {
    override fun desired(primary: Metabase, context: Context<Metabase>) = serviceMonitor {
        metadata {
            name = primary.genericResourceName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        spec = ServiceMonitorSpec(
            selector = labelSelector {
                matchLabels = primary.resourceLabelSelector
            },
            endpoints = listOf(
                EndpointSpec(
                    port = "metabase-exp",
                    interval = "10s",
                    path = ""
                )
            )
        )
    }
}
