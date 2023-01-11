package eu.glasskube.operator.api.reconciler

import io.fabric8.kubernetes.api.model.HasMetadata
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceDiscriminator
import io.javaoperatorsdk.operator.processing.event.ResourceID
import io.javaoperatorsdk.operator.processing.event.source.informer.InformerEventSource
import java.util.Optional

abstract class ResourceIdDiscriminator<R : Any, P : HasMetadata> : ResourceDiscriminator<R, P> {
    abstract fun P.getSecondaryResourceId(): ResourceID

    override fun distinguish(resource: Class<R>, primary: P, context: Context<P>): Optional<R> =
        (context.eventSourceRetriever().getResourceEventSourceFor(resource) as InformerEventSource)
            .get(primary.getSecondaryResourceId())
}
