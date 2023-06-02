package eu.glasskube.operator.infra.postgres

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BackupConfiguration(
    val barmanObjectStore: BarmanObjectStoreConfiguration? = null,
    val retentionPolicy: String? = null
)
