package eu.glasskube.operator.apps.keycloak

import eu.glasskube.operator.apps.common.backup.BackupSpec
import eu.glasskube.operator.apps.common.backup.HasBackupSpec
import eu.glasskube.operator.apps.common.database.HasDatabaseSpec
import eu.glasskube.operator.apps.common.database.postgres.PostgresDatabaseSpec
import eu.glasskube.operator.validation.Patterns.SEMVER
import io.fabric8.generator.annotation.Nullable
import io.fabric8.generator.annotation.Pattern
import io.fabric8.generator.annotation.Required
import io.fabric8.kubernetes.api.model.LocalObjectReference
import io.fabric8.kubernetes.api.model.Quantity
import io.fabric8.kubernetes.api.model.ResourceRequirements

data class KeycloakSpec(
    @field:Required
    val host: String,
    val management: ManagementSpec = ManagementSpec(),
    val resources: ResourceRequirements = defaultResourceRequirements,
    val image: String?,
    val imagePullSecrets: List<LocalObjectReference>?,
    @field:Pattern(SEMVER)
    val version: String = "21.1.2",
    @field:Nullable
    override val database: PostgresDatabaseSpec = PostgresDatabaseSpec(),
    override val backups: BackupSpec?,
    val compatibility: CompatibilitySpec? = null
) : HasBackupSpec, HasDatabaseSpec<PostgresDatabaseSpec> {
    data class ManagementSpec(val enabled: Boolean = true)

    data class CompatibilitySpec(
        val hostnameV2Enabled: Boolean = false,
        val managementPortEnabled: Boolean = false,
    )

    companion object {
        private val defaultResourceRequirements
            get() = ResourceRequirements(
                null,
                mapOf("memory" to Quantity("2", "Gi")),
                mapOf("cpu" to Quantity("200", "m"), "memory" to Quantity("400", "Mi"))
            )
    }
}
