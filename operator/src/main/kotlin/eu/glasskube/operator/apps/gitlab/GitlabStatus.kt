package eu.glasskube.operator.apps.gitlab

import eu.glasskube.operator.apps.common.database.HasReadyStatus
import eu.glasskube.operator.apps.gitlab.runner.GitlabRunnerStatus

data class GitlabStatus(
    val readyReplicas: Int,
    val postgresReady: Boolean,
    val runners: Map<String, GitlabRunnerStatus?> = emptyMap()
) : HasReadyStatus {
    override val isReady get() = readyReplicas > 0
}
