package eu.glasskube.operator.apps.common.backups.database

interface ResourceWithDatabaseBackupsSpec<T : DatabaseBackupSpec> {
    fun getSpec(): HasBackupsSpecWithDatabase<T>
}
