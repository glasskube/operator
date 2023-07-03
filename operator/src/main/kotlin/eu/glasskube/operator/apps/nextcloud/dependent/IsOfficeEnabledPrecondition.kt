package eu.glasskube.operator.apps.nextcloud.dependent

import eu.glasskube.operator.apps.nextcloud.Nextcloud
import io.fabric8.kubernetes.api.model.HasMetadata
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.dependent.DependentResource
import io.javaoperatorsdk.operator.processing.dependent.workflow.Condition

abstract class IsOfficeEnabledPrecondition<T : HasMetadata> : Condition<T, Nextcloud> {
    override fun isMet(
        dependentResource: DependentResource<T, Nextcloud>,
        primary: Nextcloud,
        context: Context<Nextcloud>
    ) = !primary.spec.apps.office?.host.isNullOrEmpty()
}
