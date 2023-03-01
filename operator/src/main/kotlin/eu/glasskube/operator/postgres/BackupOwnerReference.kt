package eu.glasskube.operator.postgres

import com.fasterxml.jackson.annotation.JsonProperty

enum class BackupOwnerReference {
    @JsonProperty("none")
    NONE,

    @JsonProperty("self")
    SELF,

    @JsonProperty("cluster")
    CLUSTER
}
