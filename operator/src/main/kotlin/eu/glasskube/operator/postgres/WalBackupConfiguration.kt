package eu.glasskube.operator.postgres

data class WalBackupConfiguration(
    val compression: CompressionType? = null,
    val encryption: EncryptionType? = null,
    val maxParallel: Int? = null
)
