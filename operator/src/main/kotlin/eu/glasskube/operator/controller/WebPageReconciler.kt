package eu.glasskube.operator.controller

import eu.glasskube.operator.resource.WebPage
import eu.glasskube.operator.resource.WebPageStatus
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration
import io.javaoperatorsdk.operator.api.reconciler.Reconciler
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl
import org.slf4j.LoggerFactory

private val LOG = LoggerFactory.getLogger(WebPageReconciler::class.java)

@ControllerConfiguration
class WebPageReconciler : Reconciler<WebPage> {
    override fun reconcile(resource: WebPage, context: Context<WebPage>): UpdateControl<WebPage> {
        LOG.info("reconciling ${resource.crdName} ${resource.apiVersion}")
        resource.status = WebPageStatus("Yum")
        return UpdateControl.patchStatus(resource)
    }
}
