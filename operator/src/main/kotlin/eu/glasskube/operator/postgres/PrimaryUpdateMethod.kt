package eu.glasskube.operator.postgres

import com.fasterxml.jackson.annotation.JsonProperty

enum class PrimaryUpdateMethod {
    @JsonProperty("switchover")
    SWITCHOVER,

    @JsonProperty("restart")
    RESTART
}
