package eu.glasskube.kubernetes.api.model

import eu.glasskube.kubernetes.api.annotation.KubernetesDslMarker
import io.fabric8.kubernetes.api.model.ObjectMeta
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder

@KubernetesDslMarker
class MetadataDsl private constructor() {
    private val builder = ObjectMetaBuilder(true)

    fun name(name: String) {
        builder.withName(name)
    }

    fun namespace(namespace: String?) {
        builder.withNamespace(namespace)
    }

    fun labels(labels: Map<String, String>) {
        builder.withLabels<String, String>(labels)
    }

    fun annotations(annotations: Map<String, String>) {
        builder.withAnnotations<String, String>(annotations)
    }

    private fun build(): ObjectMeta = builder.build()

    companion object {
        fun (MetadataDsl.() -> Unit).build() = MetadataDsl().apply(this).build()
    }
}
