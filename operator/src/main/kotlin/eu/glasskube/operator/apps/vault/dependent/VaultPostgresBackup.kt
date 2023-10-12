package eu.glasskube.operator.apps.vault.dependent

import eu.glasskube.operator.apps.vault.Vault
import eu.glasskube.operator.apps.vault.VaultReconciler
import eu.glasskube.operator.generic.dependent.postgres.DependentPostgresScheduledBackup
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = VaultReconciler.SELECTOR)
class VaultPostgresBackup : DependentPostgresScheduledBackup<Vault>(Vault.Postgres) {
    class ReconcilePrecondition : DependentPostgresScheduledBackup.ReconcilePrecondition<Vault>()
}
