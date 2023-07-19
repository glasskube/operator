package eu.glasskube.operator.apps.vault.dependent

import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.kubernetes.api.model.rbac.roleBinding
import eu.glasskube.operator.apps.vault.Vault
import eu.glasskube.operator.apps.vault.VaultReconciler
import eu.glasskube.operator.apps.vault.genericResourceName
import eu.glasskube.operator.apps.vault.resourceLabels
import io.fabric8.kubernetes.api.model.rbac.RoleBinding
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = VaultReconciler.SELECTOR)
class VaultRoleBinding : CRUDKubernetesDependentResource<RoleBinding, Vault>(RoleBinding::class.java) {
    class ReconcilePrecondition : ServiceRegistrationEnabledCondition<RoleBinding>()

    override fun desired(primary: Vault, context: Context<Vault>) = roleBinding {
        metadata {
            name(primary.genericResourceName)
            namespace(primary.namespace)
            labels(primary.resourceLabels)
        }
        roleRef {
            name(primary.genericResourceName)
        }
        subjects {
            subject {
                kind("ServiceAccount")
                name(primary.genericResourceName)
            }
        }
    }
}
