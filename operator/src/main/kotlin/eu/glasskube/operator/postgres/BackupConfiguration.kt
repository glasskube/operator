package eu.glasskube.operator.postgres

data class BackupConfiguration(
    var barmanObjectStore: BarmanObjectStoreConfiguration? = null,
    var retentionPolicy: String? = null
)
