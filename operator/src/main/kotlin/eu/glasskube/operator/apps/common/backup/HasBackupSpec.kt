package eu.glasskube.operator.apps.common.backup

interface HasBackupSpec {
    val backups: BackupSpec?
    fun requireBackups(): BackupSpec = backups
        ?: throw IllegalStateException("backup spec is null which is unexpected. is the BackupSpecNotNullCondition missing?")
}
