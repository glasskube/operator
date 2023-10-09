package eu.glasskube.operator.apps.glitchtip

import eu.glasskube.operator.apps.common.database.HasReadyStatus

data class GlitchtipStatus(
    val readyReplicas: Int,
    val workerReadyReplicas: Int,
    val redisReady: Boolean,
    val postgresReady: Boolean
) : HasReadyStatus {
    override val isReady get() = readyReplicas > 0
}
