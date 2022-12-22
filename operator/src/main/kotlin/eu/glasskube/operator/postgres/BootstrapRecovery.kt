package eu.glasskube.operator.postgres

import io.fabric8.kubernetes.api.model.LocalObjectReference

data class BootstrapRecovery(
    var database: String,
    var owner: String,
    var backup: BackupSource? = null,
    var source: String? = null,
    var recoveryTarget: RecoveryTarget? = null,
    var secret: LocalObjectReference? = null
)
