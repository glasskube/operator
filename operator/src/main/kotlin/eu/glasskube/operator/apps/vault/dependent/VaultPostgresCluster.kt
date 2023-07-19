package eu.glasskube.operator.apps.vault.dependent

import eu.glasskube.operator.apps.vault.Vault
import eu.glasskube.operator.apps.vault.VaultReconciler
import eu.glasskube.operator.generic.condition.PostgresReadyCondition
import eu.glasskube.operator.generic.dependent.postgres.DependentPostgresCluster
import eu.glasskube.utils.resourceProperty
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = VaultReconciler.SELECTOR)
class VaultPostgresCluster : DependentPostgresCluster<Vault>(Vault.Postgres) {
    class ReadyCondition : PostgresReadyCondition<Vault>()

    private val initSql by resourceProperty()
    override val Vault.initSql get() = this@VaultPostgresCluster.initSql
    override val Vault.storageSize get() = "10Gi"
}
