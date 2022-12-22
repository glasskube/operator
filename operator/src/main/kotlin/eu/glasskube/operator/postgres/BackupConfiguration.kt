package eu.glasskube.operator.postgres

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class BackupConfiguration @JsonCreator constructor(
    @JsonProperty("barmanObjectStore")
    val barmanObjectStore: BarmanObjectStoreConfiguration? = null,
    @JsonProperty("retentionPolicy")
    val retentionPolicy: String? = null
)
