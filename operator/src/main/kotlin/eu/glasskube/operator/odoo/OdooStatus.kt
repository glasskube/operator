package eu.glasskube.operator.odoo

data class OdooStatus(
    val ready: Boolean = false,
    val demoEnabledOnInstall: Boolean? = null
)
