package eu.glasskube.operator.postgres

data class BackupConfiguration(
    val barmanObjectStore: BarmanObjectStoreConfiguration? = null,
    val retentionPolicy: String? = null
)
