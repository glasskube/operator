package eu.glasskube.operator.infra.postgres

import com.fasterxml.jackson.annotation.JsonInclude
import io.fabric8.kubernetes.api.model.SecretKeySelector

@JsonInclude(JsonInclude.Include.NON_NULL)

data class BarmanObjectStoreConfiguration(
    val destinationPath: String,
    val endpointURL: String? = null,
    val endpointCA: SecretKeySelector? = null,
    val serverName: String? = null,
    val wal: WalBackupConfiguration? = null,
    val data: DataBackupConfiguration? = null,
    val tags: Map<String, String>? = null,
    val historyTags: Map<String, String>? = null,
    val s3Credentials: S3Credentials? = null
)
