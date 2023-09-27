package eu.glasskube.operator.apps.common.backups.database

interface BackupsSpecWithDatabase<T : DatabaseBackupSpec> {
    val database: T?
}
