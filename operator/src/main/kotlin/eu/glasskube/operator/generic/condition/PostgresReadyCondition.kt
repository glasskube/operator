package eu.glasskube.operator.generic.condition

import eu.glasskube.operator.infra.postgres.PostgresCluster
import eu.glasskube.operator.infra.postgres.isReady
import io.fabric8.kubernetes.api.model.HasMetadata
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.dependent.DependentResource
import io.javaoperatorsdk.operator.processing.dependent.workflow.Condition
import kotlin.jvm.optionals.getOrDefault

/**
 * Condition that is true if this Postgres cluster has at least one ready instance
 */
abstract class PostgresReadyCondition<T : HasMetadata> : Condition<PostgresCluster, T> {
    override fun isMet(
        dependentResource: DependentResource<PostgresCluster, T>,
        primary: T,
        context: Context<T>
    ): Boolean = dependentResource.getSecondaryResource(primary, context).map { it.isReady }.getOrDefault(false)
}
