package eu.glasskube.operator.postgres

import com.fasterxml.jackson.annotation.JsonProperty

data class BootstrapConfiguration(
    var initdb: BootstrapInitDB? = null,
    var recovery: BootstrapRecovery? = null,
    @JsonProperty("pg_basebackup")
    var pgBasebackup: BootstrapPgBaseBackup? = null
)
