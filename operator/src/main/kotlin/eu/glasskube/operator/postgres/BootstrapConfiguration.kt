package eu.glasskube.operator.postgres

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class BootstrapConfiguration @JsonCreator constructor(
    @JsonProperty("initdb")
    val initdb: BootstrapInitDB? = null,
    @JsonProperty("recovery")
    val recovery: BootstrapRecovery? = null,
    @JsonProperty("pg_basebackup")
    val pgBasebackup: BootstrapPgBaseBackup? = null
)
