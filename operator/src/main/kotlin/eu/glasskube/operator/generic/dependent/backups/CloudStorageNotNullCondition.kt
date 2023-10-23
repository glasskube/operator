package eu.glasskube.operator.generic.dependent.backups

import eu.glasskube.operator.apps.common.cloudstorage.ResourceWithCloudStorage
import io.fabric8.kubernetes.api.model.HasMetadata
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.dependent.DependentResource
import io.javaoperatorsdk.operator.processing.dependent.workflow.Condition

class CloudStorageNotNullCondition<R, P> : Condition<R, P> where P : HasMetadata, P : ResourceWithCloudStorage {
    override fun isMet(dependentResource: DependentResource<R, P>, primary: P, context: Context<P>) =
        primary.getSpec().cloudStorage != null
}
