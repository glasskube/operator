package eu.glasskube.operator.apps.gitea

data class GiteaActionsSpec(
    val enabled: Boolean = false,
    val runners: List<GiteaActionRunnerSpecTemplate> = emptyList()
)
