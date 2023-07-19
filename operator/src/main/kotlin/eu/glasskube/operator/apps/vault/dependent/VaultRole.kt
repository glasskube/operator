package eu.glasskube.operator.apps.vault.dependent

import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.kubernetes.api.model.rbac.role
import eu.glasskube.operator.apps.vault.Vault
import eu.glasskube.operator.apps.vault.VaultReconciler
import eu.glasskube.operator.apps.vault.genericResourceName
import eu.glasskube.operator.apps.vault.resourceLabels
import io.fabric8.kubernetes.api.model.rbac.Role
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = VaultReconciler.SELECTOR)
class VaultRole : CRUDKubernetesDependentResource<Role, Vault>(Role::class.java) {
    class ReconcilePrecondition : ServiceRegistrationEnabledCondition<Role>()

    override fun desired(primary: Vault, context: Context<Vault>) = role {
        metadata {
            name(primary.genericResourceName)
            namespace(primary.namespace)
            labels(primary.resourceLabels)
        }
        rules {
            rule {
                apiGroups("")
                resources("pods")
                verbs("get", "update", "patch")
                resourceNames((0..<primary.spec.replicas).map { "${primary.genericResourceName}-$it" })
            }
        }
    }
}
