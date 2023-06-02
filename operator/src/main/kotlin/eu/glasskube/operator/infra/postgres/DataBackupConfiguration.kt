package eu.glasskube.operator.infra.postgres

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class DataBackupConfiguration(
    val compression: CompressionType? = null,
    val encryption: EncryptionType? = null,
    val immediateCheckpoint: Boolean? = null,
    val jobs: Int? = null
)
