package eu.glasskube.kubernetes.api.model

import eu.glasskube.kubernetes.api.annotation.KubernetesDslMarker
import io.fabric8.kubernetes.api.model.ObjectMeta
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder

@KubernetesDslMarker
class MetadataDsl private constructor() {
    private val builder = ObjectMetaBuilder()

    fun name(name: String) {
        builder.withName(name)
    }

    fun namespace(namespace: String?) {
        builder.withNamespace(namespace)
    }

    fun labels(labels: Map<String, String>) {
        builder.withLabels<String, String>(labels)
    }

    fun labels(vararg labels: Pair<String, String>) {
        labels(labels.toMap())
    }

    fun annotations(annotations: Map<String, String>) {
        builder.withAnnotations<String, String>(annotations)
    }

    fun annotations(vararg annotations: Pair<String, String>) {
        annotations(annotations.toMap())
    }

    private fun build(): ObjectMeta = builder.build()

    companion object {
        fun (MetadataDsl.() -> Unit).build() = MetadataDsl().apply(this).build()
    }
}
