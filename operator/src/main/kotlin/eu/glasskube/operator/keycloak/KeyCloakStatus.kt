package eu.glasskube.operator.keycloak

data class KeyCloakStatus(
    val conditions: List<KeycloakStatusCondition> = emptyList()
)
