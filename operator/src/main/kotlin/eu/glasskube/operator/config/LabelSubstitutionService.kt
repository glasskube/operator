package eu.glasskube.operator.config

import io.fabric8.kubernetes.api.model.HasMetadata
import org.springframework.stereotype.Service

@Service
class LabelSubstitutionService {
    fun substituteVariables(value: Map<String, String>, primary: HasMetadata): Map<String, String> =
        value.map { (key, value) -> key to value.substituteVariables(primary) }.toMap()

    private fun String.substituteVariables(primary: HasMetadata): String =
        PRIMARY_SUBSTITUTIONS.entries.fold(this) { value, (search, replace) ->
            value.replace(search, replace(primary))
        }

    companion object {
        private val PRIMARY_SUBSTITUTIONS: Map<String, HasMetadata.() -> String> = mapOf(
            "\${primary.metadata.name}" to { metadata.name },
            "\${primary.metadata.namespace}" to { metadata.namespace }
        )
    }
}
