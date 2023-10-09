package eu.glasskube.operator.webhook

import com.fasterxml.jackson.databind.ObjectMapper
import eu.glasskube.kubernetes.api.model.loggingId
import eu.glasskube.operator.apps.common.database.HasReadyStatus
import eu.glasskube.operator.exception.WebhookException
import eu.glasskube.utils.logger
import eu.glasskube.utils.responseBody
import io.fabric8.kubernetes.api.model.HasMetadata
import io.fabric8.kubernetes.client.CustomResource
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.closeQuietly
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.concurrent.TimeUnit

@Component
class WebhookService(
    private val okHttpClient: OkHttpClient,
    private val objectMapper: ObjectMapper
) {
    fun <P : CustomResource<*, *>> sendStatusWebhook(resource: P, updateControl: UpdateControl<P>): UpdateControl<P> {
        val url = resource.metadata.annotations[WEBHOOK_ANNOTATION] ?: return updateControl
        val status = resource.status

        if (status !is HasReadyStatus) {
            log.warn("{} webhook annotation found but status is not HasReadyStatus", resource.loggingId)
            return updateControl
        }

        return if (updateControl.isUpdateStatus || WEBHOOK_ERROR_ANNOTATION in resource.metadata.annotations) {
            log.debug("{} sending webhook", resource.loggingId)
            doSendWebhookAndAnnotateResource(updateControl, resource, url, status.toWebhookPayload())
        } else {
            log.debug("{} webhook configured but status did not change", resource.loggingId)
            updateControl
        }
    }

    private fun HasReadyStatus.toWebhookPayload() = WebhookPayload.from(isReady)

    private fun <P : HasMetadata> doSendWebhookAndAnnotateResource(
        updateControl: UpdateControl<P>,
        resource: P,
        url: String,
        payload: WebhookPayload
    ): UpdateControl<P> = try {
        doSendWebhook(resource, url, payload)
        log.debug("{} webhook sent", resource.loggingId)
        annotateAfterSuccess(updateControl, resource)
    } catch (e: WebhookException) {
        log.warn("${resource.loggingId} error sending webhook. scheduling for retry in 1 minute", e)
        annotateAfterError(updateControl, resource).rescheduleAfter(1, TimeUnit.MINUTES)
    }

    private fun doSendWebhook(resource: HasMetadata, url: String, payload: WebhookPayload) {
        val request = Request.Builder().url(url).post(objectMapper.responseBody(payload)).build()
        try {
            val response = okHttpClient.newCall(request).execute()
            response.closeQuietly()
            if (!response.isSuccessful) {
                throw WebhookException("error response ${response.code} calling $url for resource ${resource.loggingId}")
            }
        } catch (e: IOException) {
            throw WebhookException("failed to call $url for resource ${resource.loggingId}", e)
        }
    }

    private fun <P : HasMetadata> annotateAfterError(updateControl: UpdateControl<P>, resource: P): UpdateControl<P> =
        if (resource.metadata.annotations.put(WEBHOOK_ERROR_ANNOTATION, "true") == null) {
            updateControl.alsoUpdateResource(resource)
        } else {
            updateControl
        }

    private fun <P : HasMetadata> annotateAfterSuccess(updateControl: UpdateControl<P>, resource: P): UpdateControl<P> =
        if (resource.metadata.annotations.remove(WEBHOOK_ERROR_ANNOTATION) != null) {
            updateControl.alsoUpdateResource(resource)
        } else {
            updateControl
        }

    private fun <P : HasMetadata> UpdateControl<P>.alsoUpdateResource(resource: P) = when {
        isUpdateResource -> this
        isPatchStatus -> UpdateControl.updateResourceAndPatchStatus(resource)
        isUpdateStatus -> UpdateControl.updateResourceAndStatus(resource)
        else -> UpdateControl.updateResource(resource)
    }

    companion object {
        private val log = logger()
        private const val WEBHOOK_ANNOTATION = "glasskube.eu/webhook"
        private const val WEBHOOK_ERROR_ANNOTATION = "glasskube.eu/webhook-error"
    }
}
