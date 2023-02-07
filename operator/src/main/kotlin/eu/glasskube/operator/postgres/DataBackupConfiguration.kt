package eu.glasskube.operator.postgres

data class DataBackupConfiguration(
    val compression: CompressionType? = null,
    val encryption: EncryptionType? = null,
    val immediateCheckpoint: Boolean? = null,
    val jobs: Int? = null
)
