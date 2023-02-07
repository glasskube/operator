package eu.glasskube.operator.postgres

import io.fabric8.kubernetes.api.model.LocalObjectReference

data class BootstrapRecovery(
    val database: String,
    val owner: String,
    val backup: BackupSource? = null,
    val source: String? = null,
    val recoveryTarget: RecoveryTarget? = null,
    val secret: LocalObjectReference? = null
)
