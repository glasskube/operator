package eu.glasskube.operator.apps.gitlab

data class GitlabStatus(
    val readyReplicas: Int,
    val postgresReady: Boolean
)
