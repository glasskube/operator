package eu.glasskube.operator.infra.postgres

import com.fasterxml.jackson.annotation.JsonInclude
import io.fabric8.kubernetes.api.model.LocalObjectReference

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BootstrapPgBaseBackup(
    val source: String,
    val database: String,
    val owner: String,
    val secret: LocalObjectReference? = null
)
