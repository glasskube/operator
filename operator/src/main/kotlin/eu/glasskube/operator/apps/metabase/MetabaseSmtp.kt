package eu.glasskube.operator.apps.metabase

import io.fabric8.generator.annotation.Required
import io.fabric8.kubernetes.api.model.LocalObjectReference

data class MetabaseSmtp(
    @field:Required
    val host: String,
    val port: Int = 587,
    @field:Required
    val fromAddress: String,
    @field:Required
    val authSecret: LocalObjectReference,
    val tlsEnabled: Boolean = true
)
