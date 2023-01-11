package eu.glasskube.operator.api.reconciler

import io.fabric8.kubernetes.api.model.HasMetadata
import io.javaoperatorsdk.operator.processing.event.ResourceID

abstract class NamedResourceDiscriminator<R : HasMetadata, P : HasMetadata>(
    private val secondaryResourceNameProvider: P.() -> String
) : ResourceIdDiscriminator<R, P>() {
    override fun P.getSecondaryResourceId() = ResourceID(secondaryResourceNameProvider(), metadata.namespace)
}
