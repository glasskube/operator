package eu.glasskube.operator.keycloak

data class KeycloakRealmImportStatus(
    val conditions: List<KeycloakRealmImportStatusCondition> = emptyList()
)
