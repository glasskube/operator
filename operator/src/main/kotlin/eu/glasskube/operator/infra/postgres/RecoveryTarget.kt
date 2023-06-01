package eu.glasskube.operator.infra.postgres

data class RecoveryTarget(
    val backupID: String? = null,
    val targetTLI: String? = null,
    val targetXID: String? = null,
    val targetName: String? = null,
    val targetLSN: String? = null,
    val targetTime: String? = null,
    val targetImmediate: Boolean? = null,
    val exclusive: Boolean? = null
)
