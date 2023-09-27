package eu.glasskube.operator.apps.common.backups.database

import io.fabric8.generator.annotation.Required
import io.fabric8.kubernetes.api.model.SecretKeySelector

data class PostgresBackupsSpec(
    val schedule: String?,
    @field:Required
    val retentionPolicy: String?,
    val s3: S3Spec
) : DatabaseBackupSpec {
    data class S3Spec(
        val endpoint: String?,
        val regionSecret: SecretKeySelector?,
        @field:Required
        val bucket: String,
        @field:Required
        val accessKeySecret: SecretKeySelector,
        @field:Required
        val secretKeySecret: SecretKeySelector
    )
}
