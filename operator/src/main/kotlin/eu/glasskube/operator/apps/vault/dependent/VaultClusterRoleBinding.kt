package eu.glasskube.operator.apps.vault.dependent

import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.kubernetes.api.model.rbac.clusterRoleBinding
import eu.glasskube.operator.apps.vault.Vault
import eu.glasskube.operator.apps.vault.VaultReconciler
import eu.glasskube.operator.apps.vault.clusterRoleBindingName
import eu.glasskube.operator.apps.vault.genericResourceName
import eu.glasskube.operator.apps.vault.resourceLabels
import io.fabric8.kubernetes.api.model.rbac.ClusterRoleBinding
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDNoGCKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.source.informer.Mappers

@KubernetesDependent(labelSelector = VaultReconciler.SELECTOR)
class VaultClusterRoleBinding :
    CRUDNoGCKubernetesDependentResource<ClusterRoleBinding, Vault>(ClusterRoleBinding::class.java) {
    class ReconcilePrecondition : AutoUnsealEnabledCondition<ClusterRoleBinding>()

    override fun desired(primary: Vault, context: Context<Vault>) = clusterRoleBinding {
        metadata {
            name(primary.clusterRoleBindingName)
            labels(primary.resourceLabels)
            annotations(
                // Mapping based on owner reference is not possible for cluster-scoped resources,
                // therefore we use one of the SDKs built-in Mappers.
                Mappers.DEFAULT_ANNOTATION_FOR_NAME to primary.metadata.name,
                Mappers.DEFAULT_ANNOTATION_FOR_NAMESPACE to primary.namespace!!
            )
        }
        clusterRoleRef {
            name(AUTH_DELEGATOR_CLUSTER_ROLE_NAME)
        }
        subjects {
            subject {
                kind("ServiceAccount")
                name(primary.genericResourceName)
                namespace(primary.namespace)
            }
        }
    }

    companion object {
        private const val AUTH_DELEGATOR_CLUSTER_ROLE_NAME = "system:auth-delegator"
    }
}
