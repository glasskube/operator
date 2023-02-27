package eu.glasskube.operator.gitea

data class GiteaStatus(
    val readyReplicas: Int,
    val redisReady: Boolean,
    val postgresReady: Boolean
)
