package eu.glasskube.operator.apps.vault.dependent

import eu.glasskube.operator.apps.vault.Vault
import eu.glasskube.operator.apps.vault.VaultReconciler
import eu.glasskube.operator.config.ConfigService
import eu.glasskube.operator.generic.condition.PostgresReadyCondition
import eu.glasskube.operator.generic.dependent.postgres.DependentPostgresCluster
import eu.glasskube.utils.resourceProperty
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = VaultReconciler.SELECTOR)
class VaultPostgresCluster(configService: ConfigService) :
    DependentPostgresCluster<Vault>(Vault.Postgres, configService) {
    class ReadyCondition : PostgresReadyCondition<Vault>()

    private val initSql by resourceProperty()
    override val Vault.initSql get() = this@VaultPostgresCluster.initSql
    override val Vault.defaultStorageSize get() = "10Gi"
}
