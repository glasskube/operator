package eu.glasskube.operator.infra.postgres

data class BackupConfiguration(
    val barmanObjectStore: BarmanObjectStoreConfiguration? = null,
    val retentionPolicy: String? = null
)
