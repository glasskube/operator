package eu.glasskube.operator.apps.plane

data class PlaneStatus(
    val frontend: ComponentStatus?,
    val space: ComponentStatus?,
    val api: ComponentStatus?,
    val beatWorker: ComponentStatus?,
    val worker: ComponentStatus?,
    val database: ComponentStatus?,
    val redis: ComponentStatus?
) {
    data class ComponentStatus(val ready: Boolean)
}
