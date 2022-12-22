package eu.glasskube.operator.postgres

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.fabric8.kubernetes.api.model.SecretKeySelector

data class BarmanObjectStoreConfiguration @JsonCreator constructor(
    @JsonProperty("destinationPath")
    val destinationPath: String,
    @JsonProperty("endpointURL")
    val endpointURL: String? = null,
    @JsonProperty("endpointCA")
    val endpointCA: SecretKeySelector,
    @JsonProperty("serverName")
    val serverName: String? = null,
    @JsonProperty("wal")
    val wal: WalBackupConfiguration? = null,
    @JsonProperty("data")
    val data: DataBackupConfiguration? = null,
    @JsonProperty("tags")
    val tags: Map<String, String>? = null,
    @JsonProperty("historyTags")
    val historyTags: Map<String, String>? = null
)
