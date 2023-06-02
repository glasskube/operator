package eu.glasskube.operator.infra.postgres

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class EmbeddedObjectMetadata(
    val labels: Map<String, String>? = null,
    val annotations: Map<String, String>? = null
)
