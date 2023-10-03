package eu.glasskube.operator.generic.dependent.backups

import eu.glasskube.kubernetes.api.model.namespace
import io.fabric8.kubernetes.api.model.HasMetadata

abstract class VeleroNameMapper(private val primary: HasMetadata) {
    abstract val resourceName: String
    abstract val resourceLabels: Map<String, String>
    abstract val labelSelectors: List<Map<String, String>>
    val resourceNameWithNamespace get() = "${primary.namespace}.$resourceName"
}
