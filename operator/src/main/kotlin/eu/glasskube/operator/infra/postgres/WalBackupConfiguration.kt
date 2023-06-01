package eu.glasskube.operator.infra.postgres

data class WalBackupConfiguration(
    val compression: CompressionType? = null,
    val encryption: EncryptionType? = null,
    val maxParallel: Int? = null
)
