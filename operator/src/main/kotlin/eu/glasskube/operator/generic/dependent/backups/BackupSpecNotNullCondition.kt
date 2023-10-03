package eu.glasskube.operator.generic.dependent.backups

import eu.glasskube.operator.apps.common.backup.ResourceWithBackups
import io.fabric8.kubernetes.api.model.HasMetadata
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.dependent.DependentResource
import io.javaoperatorsdk.operator.processing.dependent.workflow.Condition

abstract class BackupSpecNotNullCondition<R, P> : Condition<R, P> where P : HasMetadata, P : ResourceWithBackups {
    override fun isMet(dependentResource: DependentResource<R, P>, primary: P, context: Context<P>) =
        primary.getSpec().backups != null
}
