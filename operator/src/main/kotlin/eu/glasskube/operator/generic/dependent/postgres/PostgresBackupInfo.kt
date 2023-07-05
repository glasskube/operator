package eu.glasskube.operator.generic.dependent.postgres

data class PostgresBackupInfo(
    val bucketName: String,
    val secretName: String
)
