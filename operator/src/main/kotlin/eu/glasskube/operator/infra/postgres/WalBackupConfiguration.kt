package eu.glasskube.operator.infra.postgres

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class WalBackupConfiguration(
    val compression: CompressionType? = null,
    val encryption: EncryptionType? = null,
    val maxParallel: Int? = null
)
