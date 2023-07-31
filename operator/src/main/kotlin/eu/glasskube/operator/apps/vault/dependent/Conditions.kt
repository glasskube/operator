package eu.glasskube.operator.apps.vault.dependent

import eu.glasskube.operator.apps.vault.Vault
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.dependent.DependentResource
import io.javaoperatorsdk.operator.processing.dependent.workflow.Condition

abstract class ServiceRegistrationEnabledCondition<T> : Condition<T, Vault> {
    override fun isMet(dependentResource: DependentResource<T, Vault>, primary: Vault, context: Context<Vault>) =
        primary.spec.serviceRegistration.enabled
}

abstract class AutoUnsealEnabledCondition<T> : Condition<T, Vault> {
    override fun isMet(
        dependentResource: DependentResource<T, Vault>,
        primary: Vault,
        context: Context<Vault>
    ) = primary.spec.autoUnseal != null
}
