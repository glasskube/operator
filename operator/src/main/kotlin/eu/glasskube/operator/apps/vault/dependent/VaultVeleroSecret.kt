package eu.glasskube.operator.apps.vault.dependent

import eu.glasskube.operator.apps.vault.Vault
import eu.glasskube.operator.apps.vault.VaultReconciler
import eu.glasskube.operator.generic.dependent.backups.BackupSpecNotNullCondition
import eu.glasskube.operator.generic.dependent.backups.DependentVeleroSecret
import io.fabric8.kubernetes.api.model.Secret
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = VaultReconciler.SELECTOR)
class VaultVeleroSecret : DependentVeleroSecret<Vault>() {
    internal class ReconcilePrecondition : BackupSpecNotNullCondition<Secret, Vault>()
}
