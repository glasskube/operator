package eu.glasskube.operator.apps.common.database

interface DatabaseSpec {
    val backups: BackupsSpec?
    val storage: StorageSpec?
}
