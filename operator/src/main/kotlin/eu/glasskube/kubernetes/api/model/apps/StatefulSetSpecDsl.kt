package eu.glasskube.kubernetes.api.model.apps

import eu.glasskube.kubernetes.api.annotation.KubernetesDslMarker
import eu.glasskube.kubernetes.api.model.apps.RollingUpdateStatefulSetStrategyDsl.Companion.build
import io.fabric8.kubernetes.api.model.LabelSelector
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim
import io.fabric8.kubernetes.api.model.PodTemplateSpec
import io.fabric8.kubernetes.api.model.apps.StatefulSetSpec
import io.fabric8.kubernetes.api.model.apps.StatefulSetSpecBuilder
import io.fabric8.kubernetes.api.model.apps.StatefulSetUpdateStrategy

@KubernetesDslMarker
class StatefulSetSpecDsl private constructor() {
    private val builder = StatefulSetSpecBuilder()

    fun selector(block: (@KubernetesDslMarker LabelSelector).() -> Unit) {
        builder.withSelector(LabelSelector().apply(block))
    }

    fun serviceName(serviceName: String) {
        builder.withServiceName(serviceName)
    }

    fun replicas(replicas: Int) {
        builder.withReplicas(replicas)
    }

    fun updateStrategyOnDelete() {
        builder.withUpdateStrategy(StatefulSetUpdateStrategy(null, "OnDelete"))
    }

    fun updateStrategyRollingUpdate(block: RollingUpdateStatefulSetStrategyDsl.() -> Unit) {
        builder.withUpdateStrategy(StatefulSetUpdateStrategy(block.build(), "RollingUpdate"))
    }

    fun volumeClaimTemplates(block: VolumeClaimTemplatesDsl.() -> Unit) {
        VolumeClaimTemplatesDsl().apply(block)
    }

    fun persistentVolumeClaimRetentionPolicy(whenScaled: String? = null, whenDeleted: String? = null) {
        builder.editPersistentVolumeClaimRetentionPolicy()
            .withWhenScaled(whenScaled)
            .withWhenDeleted(whenDeleted)
            .endPersistentVolumeClaimRetentionPolicy()
    }

    fun template(block: PodTemplateSpec.() -> Unit) {
        builder.withTemplate(PodTemplateSpec().apply(block))
    }

    private fun build(): StatefulSetSpec = builder.build()

    inner class VolumeClaimTemplatesDsl {
        fun volumeClaimTemplate(block: PersistentVolumeClaim.() -> Unit) {
            builder.addToVolumeClaimTemplates(PersistentVolumeClaim().apply(block))
        }
    }

    companion object {
        fun (StatefulSetSpecDsl.() -> Unit).build() = StatefulSetSpecDsl().apply(this).build()
    }
}
