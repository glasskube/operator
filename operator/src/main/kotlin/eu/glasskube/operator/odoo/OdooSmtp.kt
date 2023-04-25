package eu.glasskube.operator.odoo

import io.fabric8.generator.annotation.Nullable
import io.fabric8.generator.annotation.Required
import io.fabric8.kubernetes.api.model.LocalObjectReference

data class OdooSmtp(
    @field: Required
    val host: String,
    val port: Int = 587,
    @field:Required
    val fromAddress: String,
    @field:Required
    val authSecret: LocalObjectReference,
    @field:Nullable
    val fromFilter: String? = null,
    val ssl: Boolean = true
)
