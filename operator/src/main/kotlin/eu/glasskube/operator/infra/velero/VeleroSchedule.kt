package eu.glasskube.operator.infra.velero

import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Kind
import io.fabric8.kubernetes.model.annotation.Version

@Kind("Schedule")
@Group("velero.io")
@Version("v1")
class VeleroSchedule : CustomResource<VeleroSchedule.Spec, VeleroSchedule.Status>(), Namespaced {
    data class Spec(
        val schedule: String,
        val template: VeleroBackupTemplate
    )

    data class Status(
        val phase: String?
    )
}

fun veleroSchedule(block: VeleroSchedule.() -> Unit) = VeleroSchedule().apply(block)
