package eu.glasskube.operator.keycloak

import io.fabric8.kubernetes.api.model.PodTemplateSpec

data class UnsupportedSpec(
    val podTemplateSpec: PodTemplateSpec? = null
)
