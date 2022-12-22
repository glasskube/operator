package eu.glasskube.operator.postgres

data class WalBackupConfiguration(
    var compression: CompressionType? = null,
    var encryption: EncryptionType? = null,
    var maxParallel: Int? = null
)
