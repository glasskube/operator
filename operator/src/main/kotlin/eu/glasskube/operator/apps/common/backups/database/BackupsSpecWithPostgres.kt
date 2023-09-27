package eu.glasskube.operator.apps.common.backups.database

data class BackupsSpecWithPostgres(
    override val database: PostgresBackupsSpec?
) : BackupsSpecWithDatabase<PostgresBackupsSpec>
