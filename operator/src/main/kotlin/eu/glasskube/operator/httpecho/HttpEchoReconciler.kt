package eu.glasskube.operator.httpecho

import eu.glasskube.operator.httpecho.dependent.*
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
        Dependent(type = HttpEchoIngress::class),
        Dependent(type = HttpEchoPostgres::class),
        Dependent(type = HttpEchoPostgresSuperuserSecret::class),
        Dependent(type = HttpEchoPostgresAppSecret::class)
    ]
)
class HttpEchoReconciler : Reconciler<HttpEcho> {
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
