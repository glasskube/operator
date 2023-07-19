package eu.glasskube.operator.apps.vault.dependent

import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.kubernetes.api.model.serviceAccount
import eu.glasskube.operator.apps.vault.Vault
import eu.glasskube.operator.apps.vault.VaultReconciler
import eu.glasskube.operator.apps.vault.genericResourceName
import eu.glasskube.operator.apps.vault.resourceLabels
import io.fabric8.kubernetes.api.model.ServiceAccount
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = VaultReconciler.SELECTOR)
class VaultServiceAccount : CRUDKubernetesDependentResource<ServiceAccount, Vault>(ServiceAccount::class.java) {
    override fun desired(primary: Vault, context: Context<Vault>) = serviceAccount {
        metadata {
            name(primary.genericResourceName)
            namespace(primary.namespace)
            labels(primary.resourceLabels)
        }
    }
}
