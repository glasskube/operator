package eu.glasskube.operator.generic.condition

import io.fabric8.kubernetes.api.model.HasMetadata
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.dependent.DependentResource
import io.javaoperatorsdk.operator.processing.dependent.workflow.Condition

abstract class AllCompositeCondition<R, P : HasMetadata>(
    private vararg val conditions: Condition<R, P>
) : Condition<R, P> {
    override fun isMet(dependentResource: DependentResource<R, P>, primary: P, context: Context<P>) =
        conditions.all { it.isMet(dependentResource, primary, context) }
}
