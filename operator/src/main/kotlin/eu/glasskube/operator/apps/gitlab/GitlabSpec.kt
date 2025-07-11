package eu.glasskube.operator.apps.gitlab

import eu.glasskube.operator.apps.common.backup.BackupSpec
import eu.glasskube.operator.apps.common.backup.HasBackupSpec
import eu.glasskube.operator.apps.common.cloudstorage.HasCloudStorageSpec
import eu.glasskube.operator.apps.common.database.HasDatabaseSpec
import eu.glasskube.operator.apps.common.database.postgres.PostgresDatabaseSpec
import eu.glasskube.operator.apps.common.storage.GenericStorageSpec
import eu.glasskube.operator.validation.Patterns.SEMVER
import io.fabric8.generator.annotation.Nullable
import io.fabric8.generator.annotation.Pattern
import io.fabric8.generator.annotation.Required
import io.fabric8.kubernetes.api.model.Quantity
import io.fabric8.kubernetes.api.model.ResourceRequirements
import io.fabric8.kubernetes.api.model.SecretKeySelector

data class GitlabSpec(
    @field:Required
    val host: String,
    val sshHost: String?,
    val sshEnabled: Boolean = true,
    val initialRootPasswordSecret: SecretKeySelector?,
    @field:Nullable
    val smtp: GitlabSmtp?,
    val runners: List<GitlabRunnerSpecTemplate> = emptyList(),
    val resources: ResourceRequirements = ResourceRequirements(
        null,
        mapOf("memory" to Quantity("3", "Gi")),
        mapOf("cpu" to Quantity("200", "m"), "memory" to Quantity("2", "Gi"))
    ),
    @field:Nullable
    val omnibusConfigOverride: String?,
    @field:Nullable
    val registry: GitlabRegistrySpec?,
    @field:Pattern(SEMVER)
    val version: String = "16.2.5",
    @field:Nullable
    override val database: PostgresDatabaseSpec = PostgresDatabaseSpec(),
    override val backups: BackupSpec?,
    val storage: GenericStorageSpec?
) : HasBackupSpec, HasCloudStorageSpec, HasDatabaseSpec<PostgresDatabaseSpec> {
    override val cloudStorage get() = registry?.storage?.s3
}
