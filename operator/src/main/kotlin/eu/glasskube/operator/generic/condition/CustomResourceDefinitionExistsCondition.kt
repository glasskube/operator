package eu.glasskube.operator.generic.condition

import eu.glasskube.kubernetes.client.exists
import eu.glasskube.kubernetes.client.resources
import io.fabric8.kubernetes.api.model.HasMetadata
import io.fabric8.kubernetes.api.model.apiextensions.v1.CustomResourceDefinition
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.dependent.DependentResource
import io.javaoperatorsdk.operator.processing.dependent.workflow.Condition

abstract class CustomResourceDefinitionExistsCondition<R, P : HasMetadata>(private val name: String) : Condition<R, P> {
    override fun isMet(dependentResource: DependentResource<R, P>, primary: P, context: Context<P>) =
        context.client.resources<CustomResourceDefinition>().withName(name).exists()
}
