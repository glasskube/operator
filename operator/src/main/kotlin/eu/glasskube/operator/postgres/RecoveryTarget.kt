package eu.glasskube.operator.postgres

data class RecoveryTarget(
    var backupID: String? = null,
    var targetTLI: String? = null,
    var targetXID: String? = null,
    var targetName: String? = null,
    var targetLSN: String? = null,
    var targetTime: String? = null,
    var targetImmediate: Boolean? = null,
    var exclusive: Boolean? = null
)
