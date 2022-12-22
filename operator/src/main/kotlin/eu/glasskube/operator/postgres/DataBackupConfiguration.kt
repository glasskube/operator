package eu.glasskube.operator.postgres

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class DataBackupConfiguration @JsonCreator constructor(
    @JsonProperty("compression")
    val compression: CompressionType? = null,
    @JsonProperty("encryption")
    val encryption: EncryptionType? = null,
    @JsonProperty("immediateCheckpoint")
    val immediateCheckpoint: Boolean? = null,
    @JsonProperty("jobs")
    val jobs: Int? = null
)
