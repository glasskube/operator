package eu.glasskube.operator.infra.postgres

import com.fasterxml.jackson.annotation.JsonProperty

enum class EncryptionType {
    @JsonProperty("")
    DEFAULT,

    @JsonProperty("AES256")
    AES256,

    @JsonProperty("aws:kms")
    KMS
}
