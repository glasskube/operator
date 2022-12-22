package eu.glasskube.operator.postgres

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class WalBackupConfiguration @JsonCreator constructor(
    @JsonProperty("compression")
    val compression: CompressionType? = null,
    @JsonProperty("encryption")
    val encryption: EncryptionType? = null,
    @JsonProperty("maxParallel")
    val maxParallel: Int? = null
)
