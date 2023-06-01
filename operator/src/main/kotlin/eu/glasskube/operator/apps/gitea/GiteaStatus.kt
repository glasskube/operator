package eu.glasskube.operator.apps.gitea

data class GiteaStatus(
    val readyReplicas: Int,
    val redisReady: Boolean,
    val postgresReady: Boolean
)
