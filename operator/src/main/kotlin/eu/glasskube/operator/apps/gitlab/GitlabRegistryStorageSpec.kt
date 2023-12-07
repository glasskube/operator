package eu.glasskube.operator.apps.gitlab

import eu.glasskube.operator.apps.common.cloudstorage.CloudStorageSpec
import io.fabric8.generator.annotation.Required
import io.fabric8.kubernetes.api.model.SecretKeySelector

data class GitlabRegistryStorageSpec(
    val s3: S3
) {
    data class S3(
        @field:Required
        override val bucket: String,
        @field:Required
        override val accessKeySecret: SecretKeySelector,
        @field:Required
        override val secretKeySecret: SecretKeySelector,
        @field:Required
        override val region: String,
        override val hostname: String,
        override val port: Int?,
        override val useSsl: Boolean,
        override val usePathStyle: Boolean = false
    ) : CloudStorageSpec
}
