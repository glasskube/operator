package eu.glasskube.operator.processing

import io.javaoperatorsdk.operator.processing.event.ResourceID
import io.javaoperatorsdk.operator.processing.event.source.SecondaryToPrimaryMapper

/**
 * An implementation of SecondaryToPrimaryMapper that delegates mapping to other SecondaryToPrimaryMappers, calling
 * them in order and returning the first result that is not empty.
 */
class CompositeSecondaryToPrimaryMapper<R>(
    private vararg val delegateMappers: SecondaryToPrimaryMapper<R>
) : SecondaryToPrimaryMapper<R> {
    override fun toPrimaryResourceIDs(dependentResource: R): MutableSet<ResourceID> =
        delegateMappers.asSequence()
            .mapNotNull { it.toPrimaryResourceIDs(dependentResource) }
            .firstOrNull { it.isNotEmpty() }
            .orEmpty()
            .toMutableSet()
}
