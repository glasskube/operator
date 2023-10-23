package eu.glasskube.operator.apps.common.backup

import eu.glasskube.operator.apps.common.cloudstorage.CloudStorageSpec
import io.fabric8.generator.annotation.Nullable
import io.fabric8.generator.annotation.Required
import io.fabric8.kubernetes.api.model.SecretKeySelector

data class BackupSpec(
    val schedule: String = "0 4 * * *",
    val ttl: String = "168h0m0s",
    @field:Required
    val s3: S3Spec
) {
    data class S3Spec(
        @field:Nullable
        override val hostname: String?,
        @field:Nullable
        override val port: Int?,
        override val useSsl: Boolean = true,
        @field:Nullable
        override val region: String?,
        @field:Required
        override val bucket: String,
        @field:Required
        override val accessKeySecret: SecretKeySelector,
        @field:Required
        override val secretKeySecret: SecretKeySelector,
        override val usePathStyle: Boolean = true
    ) : CloudStorageSpec
}
