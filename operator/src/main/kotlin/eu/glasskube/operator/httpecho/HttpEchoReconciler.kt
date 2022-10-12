package eu.glasskube.operator.httpecho

import eu.glasskube.operator.httpecho.dependent.HttpEchoDeployment
import eu.glasskube.operator.httpecho.dependent.HttpEchoIngress
import eu.glasskube.operator.httpecho.dependent.HttpEchoService
import io.fabric8.kubernetes.client.KubernetesClient
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration
import io.javaoperatorsdk.operator.api.reconciler.Reconciler
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl
import io.javaoperatorsdk.operator.api.reconciler.dependent.Dependent
import org.slf4j.LoggerFactory

private val LOG = LoggerFactory.getLogger(HttpEchoReconciler::class.java)

@ControllerConfiguration(
    dependents = [
        Dependent(type = HttpEchoDeployment::class),
        Dependent(type = HttpEchoService::class),
        Dependent(type = HttpEchoIngress::class)
    ]
)
class HttpEchoReconciler(val kubernetesClient: KubernetesClient) : Reconciler<HttpEcho> {
    override fun reconcile(resource: HttpEcho, context: Context<HttpEcho>): UpdateControl<HttpEcho> {
        LOG.info("reconciling ${resource.crdName} ${resource.apiVersion}")
        resource.status = HttpEchoStatus("Echoing")
        return UpdateControl.patchStatus(resource)
    }

    companion object {
        const val LABEL = "glasskube.eu/HttpEcho"
        const val APP_NAME = "http-echo"
        const val SELECTOR = "app.kubernetes.io/managed-by=glasskube-operator,app=$APP_NAME"
    }
}
