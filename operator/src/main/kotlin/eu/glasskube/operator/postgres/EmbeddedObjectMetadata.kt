package eu.glasskube.operator.postgres

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class EmbeddedObjectMetadata @JsonCreator constructor(
    @JsonProperty("labels")
    val labels: Map<String, String>? = null,
    @JsonProperty("annotations")
    val annotations: Map<String, String>? = null
)
