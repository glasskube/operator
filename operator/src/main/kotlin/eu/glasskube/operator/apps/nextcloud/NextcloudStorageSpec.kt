package eu.glasskube.operator.apps.nextcloud

import io.fabric8.generator.annotation.Nullable
import io.fabric8.generator.annotation.Required
import io.fabric8.kubernetes.api.model.SecretKeySelector

data class NextcloudStorageSpec(
    val s3: S3?
) {
    data class S3(
        @field:Required
        val bucket: String,
        @field:Required
        val accessKeySecret: SecretKeySelector,
        @field:Required
        val secretKeySecret: SecretKeySelector,
        @field:Nullable
        val region: String?,
        @field:Nullable
        val hostname: String?,
        @field:Nullable
        val port: Int?,
        @field:Nullable
        val objectPrefix: String?,
        @field:Nullable
        val autoCreate: Boolean?,
        val useSsl: Boolean = true,
        @field:Nullable
        val usePathStyle: Boolean?,
        @field:Nullable
        val legacyAuth: Boolean?
    )
}
