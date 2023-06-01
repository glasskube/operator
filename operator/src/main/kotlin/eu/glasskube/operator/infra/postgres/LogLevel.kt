package eu.glasskube.operator.infra.postgres

import com.fasterxml.jackson.annotation.JsonProperty

enum class LogLevel {
    @JsonProperty("error")
    ERROR,

    @JsonProperty("warning")
    WARNING,

    @JsonProperty("info")
    INFO,

    @JsonProperty("debug")
    DEBUG,

    @JsonProperty("trace")
    TRACE
}
