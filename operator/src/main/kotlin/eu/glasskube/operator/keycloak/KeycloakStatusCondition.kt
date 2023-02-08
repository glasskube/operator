package eu.glasskube.operator.keycloak

data class KeycloakStatusCondition(
    val type: String,
    val status: Boolean,
    val message: String? = null
)
