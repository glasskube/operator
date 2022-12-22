package eu.glasskube.operator.postgres

import io.fabric8.kubernetes.api.model.SecretKeySelector

data class BarmanObjectStoreConfiguration(
    var destinationPath: String,
    var endpointURL: String? = null,
    var endpointCA: SecretKeySelector,
    var serverName: String? = null,
    var wal: WalBackupConfiguration? = null,
    var data: DataBackupConfiguration? = null,
    var tags: Map<String, String>? = null,
    var historyTags: Map<String, String>? = null
)
