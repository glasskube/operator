package eu.glasskube.operator.apps.vault

import com.fasterxml.jackson.annotation.JsonPropertyDescription
import eu.glasskube.operator.apps.common.backup.BackupSpec
import eu.glasskube.operator.apps.common.backup.HasBackupSpec
import eu.glasskube.operator.apps.common.database.HasDatabaseSpec
import eu.glasskube.operator.apps.common.database.postgres.PostgresDatabaseSpec
import eu.glasskube.operator.apps.common.storage.StorageSpec
import eu.glasskube.operator.validation.Patterns
import io.fabric8.generator.annotation.Nullable
import io.fabric8.generator.annotation.Pattern
import io.fabric8.generator.annotation.Required
import io.fabric8.kubernetes.api.model.Quantity
import io.fabric8.kubernetes.api.model.ResourceRequirements
import io.fabric8.kubernetes.api.model.SecretKeySelector

data class VaultSpec(
    @field:Required
    val host: String,
    val replicas: Int = 3,
    val ui: UiSpec = UiSpec(),
    val resources: ResourceRequirements = defaultResourceRequirements,
    val serviceRegistration: ServiceRegistrationSpec = ServiceRegistrationSpec(),
    @field:Nullable
    val autoUnseal: AutoUnsealSpec?,
    @field:Nullable
    val auditStorage: AuditStorageSpec = AuditStorageSpec(),
    @field:Pattern(Patterns.SEMVER)
    val version: String = "1.14.2",
    @field:Nullable
    override val database: PostgresDatabaseSpec = PostgresDatabaseSpec(),
    override val backups: BackupSpec?
) : HasBackupSpec, HasDatabaseSpec<PostgresDatabaseSpec> {

    data class UiSpec(
        @field:Required
        val enabled: Boolean = true
    )

    data class ServiceRegistrationSpec(
        @field:Required
        val enabled: Boolean = true
    )

    data class AutoUnsealSpec(
        @field:Required
        val address: String,
        @field:Nullable
        val tlsCaSecret: SecretKeySelector?,
        @field:JsonPropertyDescription("Optional. Default is \"kubernetes\".")
        val authPath: String = "kubernetes",
        @field:JsonPropertyDescription("Optional. Default is \"namespace.name\".")
        val roleName: String?,
        @field:JsonPropertyDescription("Optional. Default is \"transit\".")
        val mountPath: String = "transit",
        @field:JsonPropertyDescription("Optional. Default is \"namespace.name\".")
        val keyName: String?
    )

    data class AuditStorageSpec(
        @field:Required
        val enabled: Boolean = false,
        override val size: Quantity = Quantity("1", "Gi"),
        override val storageClassName: String? = null,
    ) : StorageSpec

    companion object {
        private val defaultResourceRequirements
            get() = ResourceRequirements(
                null,
                mapOf("memory" to Quantity("100", "Mi")),
                mapOf("memory" to Quantity("30", "Mi"))
            )
    }
}
