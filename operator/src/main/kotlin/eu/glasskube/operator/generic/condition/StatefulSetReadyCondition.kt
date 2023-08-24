package eu.glasskube.operator.generic.condition

import io.fabric8.kubernetes.api.model.HasMetadata
import io.fabric8.kubernetes.api.model.apps.StatefulSet
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.dependent.DependentResource
import io.javaoperatorsdk.operator.processing.dependent.workflow.Condition
import kotlin.jvm.optionals.getOrDefault

abstract class StatefulSetReadyCondition<T : HasMetadata> : Condition<StatefulSet, T> {
    override fun isMet(dependentResource: DependentResource<StatefulSet, T>, primary: T, context: Context<T>) =
        dependentResource.getSecondaryResource(primary, context)
            .map { it.isReady }
            .getOrDefault(false)
}

enum class StatefulSetState {
    DOWN, DEGRADED, RUNNING
}

val StatefulSet.state
    get() = when (status?.readyReplicas) {
        null, 0 -> StatefulSetState.DOWN
        spec.replicas -> StatefulSetState.RUNNING
        else -> StatefulSetState.DEGRADED
    }

val StatefulSet.isReady
    get() = state == StatefulSetState.RUNNING
