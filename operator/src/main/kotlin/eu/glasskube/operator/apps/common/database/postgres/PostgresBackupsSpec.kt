package eu.glasskube.operator.apps.common.database.postgres

import eu.glasskube.operator.apps.common.database.BackupsSpec
import io.fabric8.generator.annotation.Required
import io.fabric8.kubernetes.api.model.SecretKeySelector

data class PostgresBackupsSpec(
    val enabled: Boolean = true,
    val schedule: String?,
    val retentionPolicy: String?,
    val s3: S3Spec?
) : BackupsSpec {
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
