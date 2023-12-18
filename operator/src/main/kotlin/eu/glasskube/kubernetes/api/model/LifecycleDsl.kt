package eu.glasskube.kubernetes.api.model

import eu.glasskube.kubernetes.api.annotation.KubernetesDslMarker
import eu.glasskube.kubernetes.api.model.LifecycleHandlerDsl.Companion.build
import io.fabric8.kubernetes.api.model.Container
import io.fabric8.kubernetes.api.model.Lifecycle
import io.fabric8.kubernetes.api.model.LifecycleBuilder

inline fun Container.lifecycle(block: LifecycleDsl.() -> Unit) {
    lifecycle = LifecycleDsl().apply(block).build()
}

@KubernetesDslMarker
class LifecycleDsl {
    private val builder = LifecycleBuilder()

    fun postStart(block: LifecycleHandlerDsl.() -> Unit) {
        builder.withPostStart(block.build())
    }

    fun preStop(block: LifecycleHandlerDsl.() -> Unit) {
        builder.withPreStop(block.build())
    }

    fun build(): Lifecycle = builder.build()
}
