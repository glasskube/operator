package eu.glasskube.operator.apps.httpecho

import eu.glasskube.operator.api.reconciler.informerEventSource
import eu.glasskube.operator.apps.httpecho.dependent.HttpEchoDeployment
import eu.glasskube.operator.apps.httpecho.dependent.HttpEchoIngress
import eu.glasskube.operator.apps.httpecho.dependent.HttpEchoService
import eu.glasskube.operator.logger
import io.fabric8.kubernetes.api.model.Secret
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration
import io.javaoperatorsdk.operator.api.reconciler.EventSourceContext
import io.javaoperatorsdk.operator.api.reconciler.EventSourceInitializer
import io.javaoperatorsdk.operator.api.reconciler.Reconciler
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl
import io.javaoperatorsdk.operator.api.reconciler.dependent.Dependent

@ControllerConfiguration(
    dependents = [
        Dependent(type = HttpEchoDeployment::class),
        Dependent(type = HttpEchoService::class),
        Dependent(type = HttpEchoIngress::class)
    ]
)
class HttpEchoReconciler : Reconciler<HttpEcho>, EventSourceInitializer<HttpEcho> {
    override fun reconcile(resource: HttpEcho, context: Context<HttpEcho>): UpdateControl<HttpEcho> {
        log.info("reconciling ${resource.crdName} ${resource.apiVersion}")
        resource.status = HttpEchoStatus("Echoing")
        return UpdateControl.patchStatus(resource)
    }

    override fun prepareEventSources(context: EventSourceContext<HttpEcho>) = with(context) {
        mutableMapOf(
            SECRETS_EVENT_SOURCE_NAME to informerEventSource<Secret>()
        )
    }

    companion object {
        private val log = logger()
        const val LABEL = "glasskube.eu/HttpEcho"
        const val APP_NAME = "http-echo"
        const val SELECTOR = "app.kubernetes.io/managed-by=glasskube-operator,app=$APP_NAME"
        const val SECRETS_EVENT_SOURCE_NAME = "HttpEchoSecretEventSource"
    }
}
