package eu.glasskube.operator.postgres

import com.fasterxml.jackson.annotation.JsonProperty

data class BootstrapConfiguration(
    val initdb: BootstrapInitDB? = null,
    val recovery: BootstrapRecovery? = null,
    @JsonProperty("pg_basebackup")
    val pgBasebackup: BootstrapPgBaseBackup? = null
)
