package eu.glasskube.operator.apps.common.backups.database

interface HasBackupsSpecWithDatabase<T : DatabaseBackupSpec> {
    val backups: BackupsSpecWithDatabase<T>?
}
