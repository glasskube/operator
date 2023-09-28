package eu.glasskube.operator.apps.keycloak

import eu.glasskube.operator.apps.common.database.HasDatabaseSpec
import eu.glasskube.operator.apps.common.database.postgres.PostgresDatabaseSpec
import eu.glasskube.operator.validation.Patterns.SEMVER
import io.fabric8.generator.annotation.Nullable
import io.fabric8.generator.annotation.Pattern
import io.fabric8.generator.annotation.Required
import io.fabric8.kubernetes.api.model.Quantity
import io.fabric8.kubernetes.api.model.ResourceRequirements

data class KeycloakSpec(
    @field:Required
    val host: String,
    val management: ManagementSpec = ManagementSpec(),
    val resources: ResourceRequirements = defaultResourceRequirements,
    @field:Pattern(SEMVER)
    val version: String = "21.1.2",
    @field:Nullable
    override val database: PostgresDatabaseSpec = PostgresDatabaseSpec()
) : HasDatabaseSpec<PostgresDatabaseSpec> {
    data class ManagementSpec(val enabled: Boolean = true)

    companion object {
        private val defaultResourceRequirements
            get() = ResourceRequirements(
                null,
                mapOf("memory" to Quantity("3", "Gi")),
                mapOf("cpu" to Quantity("200", "m"), "memory" to Quantity("2", "Gi"))
            )
    }
}
