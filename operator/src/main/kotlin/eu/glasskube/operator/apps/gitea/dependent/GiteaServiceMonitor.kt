package eu.glasskube.operator.apps.gitea.dependent

import eu.glasskube.kubernetes.api.model.labelSelector
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.secretKeySelector
import eu.glasskube.operator.apps.gitea.Gitea
import eu.glasskube.operator.apps.gitea.GiteaReconciler
import eu.glasskube.operator.apps.gitea.genericResourceName
import eu.glasskube.operator.apps.gitea.resourceLabelSelector
import eu.glasskube.operator.apps.gitea.resourceLabels
import eu.glasskube.operator.apps.gitea.secretName
import eu.glasskube.operator.infra.prometheus.servicemonitor.EndpointSpec
import eu.glasskube.operator.infra.prometheus.servicemonitor.ServiceMonitor
import eu.glasskube.operator.infra.prometheus.servicemonitor.ServiceMonitorSpec
import eu.glasskube.operator.infra.prometheus.servicemonitor.serviceMonitor
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
