package eu.glasskube.operator.postgres

data class DataBackupConfiguration(
    var compression: CompressionType? = null,
    var encryption: EncryptionType? = null,
    var immediateCheckpoint: Boolean? = null,
    var jobs: Int? = null
)
