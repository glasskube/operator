package eu.glasskube.kubernetes.api.model

import eu.glasskube.kubernetes.api.model.MetadataDsl.Companion.build
import io.fabric8.kubernetes.api.model.ServiceAccount
import io.fabric8.kubernetes.api.model.ServiceAccountBuilder

inline fun serviceAccount(block: ServiceAccountDsl.() -> Unit) = ServiceAccountDsl().apply(block).build()

class ServiceAccountDsl {
    private val builder = ServiceAccountBuilder()

    fun metadata(block: MetadataDsl.() -> Unit) {
        builder.withMetadata(block.build())
    }

    fun build(): ServiceAccount = builder.build()
}
