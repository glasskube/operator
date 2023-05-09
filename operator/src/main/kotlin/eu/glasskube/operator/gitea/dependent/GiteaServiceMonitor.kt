package eu.glasskube.operator.gitea.dependent

import eu.glasskube.kubernetes.api.model.labelSelector
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.secretKeySelector
import eu.glasskube.operator.gitea.Gitea
import eu.glasskube.operator.gitea.GiteaReconciler
import eu.glasskube.operator.gitea.genericResourceName
import eu.glasskube.operator.gitea.resourceLabelSelector
import eu.glasskube.operator.gitea.resourceLabels
import eu.glasskube.operator.gitea.secretName
import eu.glasskube.operator.prometheus.servicemonitor.EndpointSpec
import eu.glasskube.operator.prometheus.servicemonitor.ServiceMonitor
import eu.glasskube.operator.prometheus.servicemonitor.ServiceMonitorSpec
import eu.glasskube.operator.prometheus.servicemonitor.serviceMonitor
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = GiteaReconciler.SELECTOR)
class GiteaServiceMonitor : CRUDKubernetesDependentResource<ServiceMonitor, Gitea>(ServiceMonitor::class.java) {
    override fun desired(primary: Gitea, context: Context<Gitea>) = serviceMonitor {
        metadata {
            name = primary.genericResourceName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        spec = ServiceMonitorSpec(
            endpoints = listOf(
                EndpointSpec(
                    port = "http",
                    path = "/metrics",
                    interval = "10s",
                    bearerTokenSecret = secretKeySelector(primary.secretName, "GITEA__metrics__TOKEN")
                )
            ),
            selector = labelSelector {
                matchLabels = primary.resourceLabelSelector
            }
        )
    }
}
