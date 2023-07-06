package eu.glasskube.operator.apps.keycloak

data class KeycloakStatus(
    val readyInstances: Int,
    val postgresReady: Boolean
)
