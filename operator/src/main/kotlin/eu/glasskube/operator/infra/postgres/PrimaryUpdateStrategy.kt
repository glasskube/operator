package eu.glasskube.operator.infra.postgres

import com.fasterxml.jackson.annotation.JsonProperty

enum class PrimaryUpdateStrategy {
    @JsonProperty("unsupervised")
    UNSUPERVISED,

    @JsonProperty("supervised")
    SUPERVISED
}
