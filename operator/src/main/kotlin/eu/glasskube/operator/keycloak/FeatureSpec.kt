package eu.glasskube.operator.keycloak

data class FeatureSpec(
    val enabledFeatures: List<String>? = null,
    val disabledFeatures: List<String>? = null
)
