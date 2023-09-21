package eu.glasskube.kubernetes.api.model.apps

import eu.glasskube.kubernetes.api.model.intOrString
import io.fabric8.kubernetes.api.model.apps.RollingUpdateStatefulSetStrategy
import io.fabric8.kubernetes.api.model.apps.RollingUpdateStatefulSetStrategyBuilder

class RollingUpdateStatefulSetStrategyDsl private constructor() {
    private val builder = RollingUpdateStatefulSetStrategyBuilder(true)

    fun partition(partition: Int) {
        builder.withPartition(partition)
    }

    fun maxUnavailable(maxUnavailable: Int) {
        builder.withMaxUnavailable(intOrString(maxUnavailable))
    }

    fun maxUnavailable(maxUnavailable: String) {
        builder.withMaxUnavailable(intOrString(maxUnavailable))
    }

    fun build(): RollingUpdateStatefulSetStrategy = builder.build()

    companion object {
        fun (RollingUpdateStatefulSetStrategyDsl.() -> Unit).build() =
            RollingUpdateStatefulSetStrategyDsl().apply(this).build()
    }
}
