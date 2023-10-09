package eu.glasskube.operator.apps.plane

import eu.glasskube.operator.apps.common.database.HasReadyStatus

data class PlaneStatus(
    val frontend: ComponentStatus?,
    val space: ComponentStatus?,
    val api: ComponentStatus?,
    val beatWorker: ComponentStatus?,
    val worker: ComponentStatus?,
    val database: ComponentStatus?,
    val redis: ComponentStatus?
) : HasReadyStatus {
    data class ComponentStatus(val ready: Boolean)

    override val isReady get() = frontend?.ready == true && api?.ready == true
}
