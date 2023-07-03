package eu.glasskube.operator.apps.nextcloud

data class NextcloudStatus(
    val readyReplicas: Int,
    val redisReady: Boolean,
    val postgresReady: Boolean,
    val officeReady: Boolean
)
