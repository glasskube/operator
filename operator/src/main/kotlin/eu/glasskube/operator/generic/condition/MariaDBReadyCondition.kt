package eu.glasskube.operator.generic.condition

import eu.glasskube.operator.infra.mariadb.MariaDB
import eu.glasskube.operator.infra.mariadb.isReady
import io.fabric8.kubernetes.api.model.HasMetadata
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.dependent.DependentResource
import io.javaoperatorsdk.operator.processing.dependent.workflow.Condition
import kotlin.jvm.optionals.getOrDefault

/**
 * Condition that is true if the MariaDB resource status has a condition with type ready and status true
 */
abstract class MariaDBReadyCondition<T : HasMetadata> : Condition<MariaDB, T> {
    override fun isMet(dependentResource: DependentResource<MariaDB, T>, primary: T, context: Context<T>): Boolean =
        dependentResource.getSecondaryResource(primary, context).map { it.isReady }.getOrDefault(false)
}
