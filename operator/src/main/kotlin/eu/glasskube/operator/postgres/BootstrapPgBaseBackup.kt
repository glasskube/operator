package eu.glasskube.operator.postgres

import io.fabric8.kubernetes.api.model.LocalObjectReference

data class BootstrapPgBaseBackup(
    val source: String,
    val database: String,
    val owner: String,
    val secret: LocalObjectReference? = null
)
