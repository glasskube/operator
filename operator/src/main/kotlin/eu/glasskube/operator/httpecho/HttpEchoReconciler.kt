package eu.glasskube.operator.httpecho

import eu.glasskube.operator.api.reconciler.informerEventSource
import eu.glasskube.operator.httpecho.dependent.HttpEchoDeployment
import eu.glasskube.operator.httpecho.dependent.HttpEchoIngress
import eu.glasskube.operator.httpecho.dependent.HttpEchoService
import io.fabric8.kubernetes.api.model.Secret
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration
import io.javaoperatorsdk.operator.api.reconciler.EventSourceContext
import io.javaoperatorsdk.operator.api.reconciler.EventSourceInitializer
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
class HttpEchoReconciler : Reconciler<HttpEcho>, EventSourceInitializer<HttpEcho> {
    override fun reconcile(resource: HttpEcho, context: Context<HttpEcho>): UpdateControl<HttpEcho> {
        LOG.info("reconciling ${resource.crdName} ${resource.apiVersion}")
        resource.status = HttpEchoStatus("Echoing")
        return UpdateControl.patchStatus(resource)
    }

    override fun prepareEventSources(context: EventSourceContext<HttpEcho>) = with(context) {
        mutableMapOf(
            SECRETS_EVENT_SOURCE_NAME to informerEventSource<Secret>()
        )
    }

    companion object {
        const val LABEL = "glasskube.eu/HttpEcho"
        const val APP_NAME = "http-echo"
        const val SELECTOR = "app.kubernetes.io/managed-by=glasskube-operator,app=$APP_NAME"
        const val SECRETS_EVENT_SOURCE_NAME = "HttpEchoSecretEventSource"
    }
}
