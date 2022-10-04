package eu.glasskube.operator.echo.controller

import eu.glasskube.operator.resource.HttpEcho
import eu.glasskube.operator.resource.HttpEchoDeployment
import eu.glasskube.operator.resource.HttpEchoStatus
import io.fabric8.kubernetes.client.KubernetesClient
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration
import io.javaoperatorsdk.operator.api.reconciler.Reconciler
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl
import io.javaoperatorsdk.operator.api.reconciler.dependent.Dependent
import org.slf4j.LoggerFactory

private val LOG = LoggerFactory.getLogger(HttpEchoReconciler::class.java)
private const val ECHO = "echo";


@ControllerConfiguration(
    dependents = [
        Dependent(type = HttpEchoDeployment::class),
//        Dependent(type = HttpEchoService::class),
//        Dependent(type = HttpEchoIngress::class)
    ]
)
class HttpEchoReconciler(val kubernetesClient: KubernetesClient) : Reconciler<HttpEcho> {

    override fun reconcile(resource: HttpEcho, context: Context<HttpEcho>): UpdateControl<HttpEcho> {
        LOG.info("reconciling ${resource.crdName} ${resource.apiVersion}")
        resource.status = HttpEchoStatus("Echoing")
        createDeployment()

        return UpdateControl.patchStatus(resource)
    }

    private fun createDeployment() {

    }
}
