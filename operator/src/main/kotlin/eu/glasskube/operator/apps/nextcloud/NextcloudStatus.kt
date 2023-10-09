package eu.glasskube.operator.apps.nextcloud

import eu.glasskube.operator.apps.common.database.HasReadyStatus

data class NextcloudStatus(
    val readyReplicas: Int,
    val redisReady: Boolean,
    val postgresReady: Boolean,
    val officeReady: Boolean
) : HasReadyStatus {
    override val isReady get() = readyReplicas > 0
}
