package eu.glasskube.operator.apps.gitlab.dependent

import eu.glasskube.operator.apps.gitlab.Gitlab
import io.fabric8.kubernetes.api.model.HasMetadata
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.dependent.DependentResource
import io.javaoperatorsdk.operator.processing.dependent.workflow.Condition

abstract class GitlabRegistryEnabledPrecondition<T : HasMetadata> : Condition<T, Gitlab> {
    override fun isMet(
        dependentResource: DependentResource<T, Gitlab>,
        primary: Gitlab,
        context: Context<Gitlab>
    ) = !primary.spec.registry?.host.isNullOrEmpty()
}
