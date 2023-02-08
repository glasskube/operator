package eu.glasskube.operator.keycloak

import io.fabric8.kubernetes.api.model.SecretKeySelector

data class DatabaseSpec(
    val vendor: String,
    val usernameSecret: SecretKeySelector,
    val passwordSecret: SecretKeySelector,
    val database: String? = null,
    val host: String? = null,
    val port: Int? = null,
    val schema: String? = null,
    val url: String? = null,
    val poolInitialSize: Int? = null,
    val poolMaxSize: Int? = null
)
