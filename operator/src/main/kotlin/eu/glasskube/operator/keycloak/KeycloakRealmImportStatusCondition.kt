package eu.glasskube.operator.keycloak

data class KeycloakRealmImportStatusCondition(
    val type: String,
    val status: Boolean,
    val message: String? = null
)
