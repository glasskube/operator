package eu.glasskube.kubernetes.api.model.apps

import eu.glasskube.kubernetes.api.annotation.KubernetesDslMarker
import eu.glasskube.kubernetes.api.model.MetadataDsl
import eu.glasskube.kubernetes.api.model.MetadataDsl.Companion.build
import eu.glasskube.kubernetes.api.model.apps.StatefulSetSpecDsl.Companion.build
import io.fabric8.kubernetes.api.model.apps.StatefulSet
import io.fabric8.kubernetes.api.model.apps.StatefulSetBuilder

fun statefulSet(block: StatefulSetDsl.() -> Unit): StatefulSet = StatefulSetDsl().apply(block).build()

@KubernetesDslMarker
class StatefulSetDsl {
    private val builder = StatefulSetBuilder()

    fun metadata(block: MetadataDsl.() -> Unit) {
        builder.withMetadata(block.build())
    }

    fun spec(block: StatefulSetSpecDsl.() -> Unit) {
        builder.withSpec(block.build())
    }

    fun build(): StatefulSet = builder.build()
}
