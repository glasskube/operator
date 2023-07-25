package eu.glasskube.operator.apps.gitlab

import io.fabric8.generator.annotation.Required
import io.fabric8.kubernetes.api.model.SecretKeySelector

data class GitlabRegistryStorageSpec(
    val s3: S3
) {
    data class S3(
        @field:Required
        val bucket: String,
        @field:Required
        val accessKeySecret: SecretKeySelector,
        @field:Required
        val secretKeySecret: SecretKeySelector,
        @field:Required
        val region: String,
        @field:Required
        val hostname: String,
        @field:Required
        val usePathStyle: Boolean
    )
}
