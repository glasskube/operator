package eu.glasskube.operator.postgres

data class EmbeddedObjectMetadata(
    val labels: Map<String, String>? = null,
    val annotations: Map<String, String>? = null
)
