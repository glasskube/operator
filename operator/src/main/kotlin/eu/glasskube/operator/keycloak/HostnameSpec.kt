package eu.glasskube.operator.keycloak

data class HostnameSpec(
    val hostname: String,
    val admin: String? = null,
    val adminUrl: String? = null,
    val strict: Boolean? = null,
    val strictBackchannel: Boolean? = null
)
