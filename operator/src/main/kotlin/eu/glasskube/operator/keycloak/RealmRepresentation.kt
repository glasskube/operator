package eu.glasskube.operator.keycloak

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class RealmRepresentation(
    val id: String? = null,
    val realm: String? = null,
    val displayName: String? = null,
    val enabled: Boolean? = null
)
