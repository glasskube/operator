package eu.glasskube.operator.apps.vault

import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.operator.Labels
import eu.glasskube.operator.apps.common.backups.database.PostgresBackupsSpec
import eu.glasskube.operator.apps.common.backups.database.ResourceWithDatabaseBackupsSpec
import eu.glasskube.operator.generic.dependent.postgres.PostgresNameMapper
import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Version

@Group("glasskube.eu")
@Version("v1alpha1")
class Vault : CustomResource<VaultSpec, VaultStatus>(), Namespaced, ResourceWithDatabaseBackupsSpec<PostgresBackupsSpec> {
    companion object {
        internal const val APP_NAME = "vault"
    }

    internal object Postgres : PostgresNameMapper<Vault>() {
        override fun getName(primary: Vault) = "${primary.genericResourceName}-db"
        override fun getLabels(primary: Vault) = primary.resourceLabels
        override fun getDatabaseName(primary: Vault) = "vault"
    }
}

internal val Vault.genericResourceName
    get() = "${Vault.APP_NAME}-${metadata.name}"
internal val Vault.resourceLabels
    get() = Labels.resourceLabels(Vault.APP_NAME, metadata.name, Vault.APP_NAME, spec.version)
internal val Vault.resourceLabelSelector
    get() = Labels.resourceLabelSelector(Vault.APP_NAME, metadata.name, Vault.APP_NAME)
internal val Vault.serviceName get() = genericResourceName
internal val Vault.headlessServiceName get() = "$genericResourceName-headless"
internal val Vault.tlsSecretName get() = "$genericResourceName-tls"
internal val Vault.databaseBackupBucketName get() = "$genericResourceName-backup"
internal val Vault.clusterRoleBindingName get() = "$namespace:$genericResourceName-auth-delegator"

internal val Vault.appImage get() = "hashicorp/${Vault.APP_NAME}:${spec.version}"
