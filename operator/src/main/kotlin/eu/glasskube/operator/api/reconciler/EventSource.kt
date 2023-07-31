package eu.glasskube.operator.api.reconciler

import io.fabric8.kubernetes.api.model.HasMetadata
import io.javaoperatorsdk.operator.api.config.informer.InformerConfiguration
import io.javaoperatorsdk.operator.api.config.informer.InformerConfiguration.InformerConfigurationBuilder
import io.javaoperatorsdk.operator.api.reconciler.EventSourceContext
import io.javaoperatorsdk.operator.processing.event.source.informer.InformerEventSource

inline fun <reified T : HasMetadata> EventSourceContext<*>.informerEventSource(
    customizer: InformerConfigurationBuilder<T>.() -> Unit = {}
) = InformerEventSource(
    InformerConfiguration.from(T::class.java, this).apply(customizer).build(),
    this
)
