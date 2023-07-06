package eu.glasskube.operator.apps.keycloak

import io.fabric8.generator.annotation.Required
import io.fabric8.kubernetes.api.model.Quantity
import io.fabric8.kubernetes.api.model.ResourceRequirements

data class KeycloakSpec(
    @field:Required
    val host: String,
    val management: ManagementSpec = ManagementSpec(),
    val resources: ResourceRequirements = defaultResourceRequirements
) {
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
