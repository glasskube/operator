package eu.glasskube.operator.apps.metabase

import eu.glasskube.operator.apps.common.database.HasReadyStatus

data class MetabaseStatus(
    val readyReplicas: Int,
    val postgresReady: Boolean
) : HasReadyStatus {
    override val isReady get() = readyReplicas > 0
}
