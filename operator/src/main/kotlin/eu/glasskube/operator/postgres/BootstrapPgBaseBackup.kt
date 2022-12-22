package eu.glasskube.operator.postgres

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.fabric8.kubernetes.api.model.LocalObjectReference

data class BootstrapPgBaseBackup @JsonCreator constructor(
    @JsonProperty("source")
    val source: String,
    @JsonProperty("database")
    val database: String,
    @JsonProperty("owner")
    val owner: String,
    @JsonProperty("secret")
    val secret: LocalObjectReference? = null
)
