package eu.glasskube.operator.apps.gitlab.dependent

import eu.glasskube.kubernetes.api.model.labelSelector
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.apps.gitlab.Gitlab
import eu.glasskube.operator.apps.gitlab.GitlabReconciler
import eu.glasskube.operator.apps.gitlab.genericResourceName
import eu.glasskube.operator.apps.gitlab.resourceLabelSelector
import eu.glasskube.operator.apps.gitlab.resourceLabels
import eu.glasskube.operator.infra.prometheus.servicemonitor.EndpointSpec
import eu.glasskube.operator.infra.prometheus.servicemonitor.ServiceMonitor
import eu.glasskube.operator.infra.prometheus.servicemonitor.ServiceMonitorSpec
import eu.glasskube.operator.infra.prometheus.servicemonitor.serviceMonitor
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = GitlabReconciler.SELECTOR)
class GitlabServiceMonitor : CRUDKubernetesDependentResource<ServiceMonitor, Gitlab>(ServiceMonitor::class.java) {
    override fun desired(primary: Gitlab, context: Context<Gitlab>) = serviceMonitor {
        metadata {
            name(primary.genericResourceName)
            namespace(primary.metadata.namespace)
            labels(primary.resourceLabels)
        }
        spec = ServiceMonitorSpec(
            selector = labelSelector {
                matchLabels = primary.resourceLabelSelector
            },
            endpoints = listOf(
                EndpointSpec(
                    port = "sidekiq-exp",
                    interval = "10s",
                    path = "/metrics"
                ),
                EndpointSpec(
                    port = "gitlab-exp",
                    interval = "10s",
                    path = "/metrics"
                ),
                EndpointSpec(
                    port = "gitaly-exp",
                    interval = "10s",
                    path = "/metrics"
                )
            )
        )
    }
}
