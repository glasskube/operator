package eu.glasskube.operator.apps.keycloak

import eu.glasskube.operator.apps.common.HasUpdatesSpec
import eu.glasskube.operator.apps.common.SemanticVersionUpdatesSpec
import io.fabric8.generator.annotation.Required
import io.fabric8.kubernetes.api.model.Quantity
import io.fabric8.kubernetes.api.model.ResourceRequirements

data class KeycloakSpec(
    @field:Required
    val host: String,
    val management: ManagementSpec = ManagementSpec(),
    val resources: ResourceRequirements = defaultResourceRequirements,
    override val updates: SemanticVersionUpdatesSpec = SemanticVersionUpdatesSpec("21.1.2")
) : HasUpdatesSpec {
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
