package eu.glasskube.operator.postgres

import io.fabric8.kubernetes.api.model.LocalObjectReference

data class BootstrapPgBaseBackup(
    var source: String,
    var database: String,
    var owner: String,
    var secret: LocalObjectReference? = null
)
