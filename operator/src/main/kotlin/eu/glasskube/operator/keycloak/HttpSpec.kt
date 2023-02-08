package eu.glasskube.operator.keycloak

data class HttpSpec(
    val tlsSecret: String? = null,
    val httpEnabled: Boolean? = null,
    val httpPort: Int? = null
)
