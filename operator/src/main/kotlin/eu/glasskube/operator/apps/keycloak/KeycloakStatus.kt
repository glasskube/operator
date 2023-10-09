package eu.glasskube.operator.apps.keycloak

import eu.glasskube.operator.apps.common.database.HasReadyStatus

data class KeycloakStatus(
    val readyInstances: Int,
    val postgresReady: Boolean
) : HasReadyStatus {
    override val isReady get() = readyInstances > 0
}
