package eu.glasskube.operator.generic.condition

import io.fabric8.kubernetes.api.model.HasMetadata
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.dependent.DependentResource
import io.javaoperatorsdk.operator.processing.dependent.workflow.Condition
import kotlin.jvm.optionals.getOrDefault

/**
 * Condition that is true if this deployment has at least one ready replica.
 */
abstract class DeploymentReadyCondition<T : HasMetadata> : Condition<Deployment, T> {
    override fun isMet(dependentResource: DependentResource<Deployment, T>, primary: T, context: Context<T>): Boolean =
        dependentResource.getSecondaryResource(primary, context)
            .map { deployment -> deployment.isReady }
            .getOrDefault(false)
}

val Deployment.isReady get() = status?.readyReplicas?.let { it > 0 } ?: false
