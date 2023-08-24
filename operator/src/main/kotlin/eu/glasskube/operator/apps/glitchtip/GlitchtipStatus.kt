package eu.glasskube.operator.apps.glitchtip

data class GlitchtipStatus(
    val readyReplicas: Int,
    val workerReadyReplicas: Int,
    val redisReady: Boolean,
    val postgresReady: Boolean
)
