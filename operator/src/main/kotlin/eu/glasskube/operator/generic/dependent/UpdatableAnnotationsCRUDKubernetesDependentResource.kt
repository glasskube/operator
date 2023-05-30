package eu.glasskube.operator.generic.dependent

import io.fabric8.kubernetes.api.model.HasMetadata
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.Matcher
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.GenericKubernetesResourceMatcher
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.ResourceUpdatePreProcessor
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.processors.GenericResourceUpdatePreProcessor

/**
 * This class of dependent resource is identical to CRUDKubernetesDependentResource, except:
 * - metadata is considered during matching and
 * - when a resource is updated, the annotations of the desired resource are applied.
 */
abstract class UpdatableAnnotationsCRUDKubernetesDependentResource<R : HasMetadata, P : HasMetadata>(resourceType: Class<R>) :
    CRUDKubernetesDependentResource<R, P>(resourceType),
    Matcher<R, P>,
    ResourceUpdatePreProcessor<R> {
    private val processorDelegate = GenericResourceUpdatePreProcessor.processorFor(resourceType)

    override fun match(actualResource: R, primary: P, context: Context<P>): Matcher.Result<R> =
        GenericKubernetesResourceMatcher.match(this, actualResource, primary, context, true)

    override fun replaceSpecOnActual(actual: R, desired: R, context: Context<*>): R =
        processorDelegate.replaceSpecOnActual(actual, desired, context)
            .apply { metadata.annotations = desired.metadata.annotations }
}
