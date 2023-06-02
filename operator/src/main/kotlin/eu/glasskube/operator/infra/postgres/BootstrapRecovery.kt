package eu.glasskube.operator.infra.postgres

import com.fasterxml.jackson.annotation.JsonInclude
import io.fabric8.kubernetes.api.model.LocalObjectReference

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BootstrapRecovery(
    val database: String,
    val owner: String,
    val backup: BackupSource? = null,
    val source: String? = null,
    val recoveryTarget: RecoveryTarget? = null,
    val secret: LocalObjectReference? = null
)
