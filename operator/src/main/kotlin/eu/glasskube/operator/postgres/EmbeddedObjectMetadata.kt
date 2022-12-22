package eu.glasskube.operator.postgres

data class EmbeddedObjectMetadata(
    var labels: Map<String, String>? = null,
    var annotations: Map<String, String>? = null
)
