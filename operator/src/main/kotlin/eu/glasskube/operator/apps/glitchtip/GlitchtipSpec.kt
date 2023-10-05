package eu.glasskube.operator.apps.glitchtip

import eu.glasskube.operator.apps.common.backup.BackupSpec
import eu.glasskube.operator.apps.common.backup.HasBackupSpec
import eu.glasskube.operator.apps.common.database.HasDatabaseSpec
import eu.glasskube.operator.apps.common.database.postgres.PostgresDatabaseSpec
import eu.glasskube.operator.validation.Patterns.SEMVER
import io.fabric8.generator.annotation.Nullable
import io.fabric8.generator.annotation.Pattern
import io.fabric8.generator.annotation.Required
import io.fabric8.kubernetes.api.model.Quantity
import io.fabric8.kubernetes.api.model.ResourceRequirements

data class GlitchtipSpec(
    @field:Required
    val host: String,
    val replicas: Int = 1,
    val registrationEnabled: Boolean = false,
    val organizationCreationEnabled: Boolean = false,
    @field:Nullable
    val smtp: GlitchtipSmtp? = null,
    val resources: ResourceRequirements = ResourceRequirements(
        null,
        mapOf("memory" to Quantity("800", "Mi")),
        mapOf("memory" to Quantity("700", "Mi"))
    ),
    @field:Pattern(SEMVER)
    val version: String = "3.4.0",
    @field:Nullable
    override val database: PostgresDatabaseSpec = PostgresDatabaseSpec(),
    override val backups: BackupSpec?
) : HasBackupSpec, HasDatabaseSpec<PostgresDatabaseSpec>
