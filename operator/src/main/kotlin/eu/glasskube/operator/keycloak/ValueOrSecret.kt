package eu.glasskube.operator.keycloak

import io.fabric8.kubernetes.api.model.SecretKeySelector

data class ValueOrSecret private constructor(
    val name: String? = null,
    val value: String? = null,
    val secret: SecretKeySelector? = null
) {
    constructor(secret: SecretKeySelector) : this(null, null, secret)
    constructor(name: String, value: String) : this(name, value, null)
}
