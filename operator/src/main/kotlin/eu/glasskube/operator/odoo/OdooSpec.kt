package eu.glasskube.operator.odoo

import io.fabric8.generator.annotation.Nullable
import io.fabric8.generator.annotation.Required

data class OdooSpec(
    @field:Required
    val host: String,
    val demoEnabled: Boolean = true,
    @field:Nullable
    val smtp: OdooSmtp? = null
)
