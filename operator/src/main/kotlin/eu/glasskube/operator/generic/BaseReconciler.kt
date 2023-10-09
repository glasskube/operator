package eu.glasskube.operator.generic

import eu.glasskube.kubernetes.api.model.loggingId
import eu.glasskube.operator.webhook.WebhookService
import eu.glasskube.utils.logger
import io.fabric8.kubernetes.client.CustomResource
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.Reconciler
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl

abstract class BaseReconciler<P : CustomResource<*, *>>(private val webhookService: WebhookService) : Reconciler<P> {
    abstract fun processReconciliation(resource: P, context: Context<P>): UpdateControl<P>

    final override fun reconcile(resource: P, context: Context<P>): UpdateControl<P> {
        log.debug("{} reconciling", resource.loggingId)
        return processReconciliation(resource, context).let { webhookService.sendStatusWebhook(resource, it) }
    }

    companion object {
        private val log = logger()
    }
}
