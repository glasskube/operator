package eu.glasskube.operator.api.reconciler

import io.fabric8.kubernetes.api.model.HasMetadata
import io.javaoperatorsdk.operator.api.config.informer.InformerConfiguration
import io.javaoperatorsdk.operator.api.config.informer.InformerConfiguration.InformerConfigurationBuilder
import io.javaoperatorsdk.operator.api.reconciler.EventSourceContext
import io.javaoperatorsdk.operator.processing.event.source.informer.InformerEventSource

/**
 * Creates a new InformerEventSource from this context.
 *
 * @param labelSelector
 * Kubernetes label selector the event source should use.
 * While not required by the Informer API, we enforce setting a label selector
 * to prevent accidentally getting too many resources from the Kubernetes API.
 * @param customizer
 * Can be used to make additional changes to the InformerConfiguration. (optional)
 */
inline fun <reified T : HasMetadata> EventSourceContext<*>.informerEventSource(
    labelSelector: String,
    customizer: InformerConfigurationBuilder<T>.() -> Unit = {}
) = InformerEventSource(
    InformerConfiguration.from(T::class.java, this)
        .withLabelSelector(labelSelector)
        .apply(customizer)
        .build(),
    this
)
