package eu.glasskube.operator.apps.matomo

import io.fabric8.generator.annotation.Required
import io.fabric8.kubernetes.api.model.LocalObjectReference

data class MatomoSmtp(
    @field:Required
    val host: String,
    val port: Int = 587,
    @field:Required
    val fromAddress: String,
    @field:Required
    val authSecret: LocalObjectReference,
    val tlsEnabled: Boolean = true
)
