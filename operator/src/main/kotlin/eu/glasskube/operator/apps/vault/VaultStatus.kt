package eu.glasskube.operator.apps.vault

import eu.glasskube.operator.apps.common.database.HasReadyStatus

data class VaultStatus(
    val readyReplicas: Int,
    val postgresReady: Boolean
) : HasReadyStatus {
    override val isReady get() = readyReplicas > 0
}
