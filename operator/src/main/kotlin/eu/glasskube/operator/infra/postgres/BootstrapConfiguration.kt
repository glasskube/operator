package eu.glasskube.operator.infra.postgres

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BootstrapConfiguration(
    val initdb: BootstrapInitDB? = null,
    val recovery: BootstrapRecovery? = null,
    @JsonProperty("pg_basebackup")
    val pgBasebackup: BootstrapPgBaseBackup? = null
)
