package eu.glasskube.operator.infra.postgres

import com.fasterxml.jackson.annotation.JsonProperty

enum class CompressionType {
    @JsonProperty("")
    DEFAULT,

    @JsonProperty("gzip")
    GZIP,

    @JsonProperty("bzip2")
    BZIP2,

    @JsonProperty("snappy")
    SNAPPY
}
