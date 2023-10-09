package eu.glasskube.operator.apps.gitea

import eu.glasskube.operator.apps.common.database.HasReadyStatus

data class GiteaStatus(
    val readyReplicas: Int,
    val redisReady: Boolean,
    val postgresReady: Boolean
) : HasReadyStatus {
    override val isReady get() = readyReplicas > 0
}
