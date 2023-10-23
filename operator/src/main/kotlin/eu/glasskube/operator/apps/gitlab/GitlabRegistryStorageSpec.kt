package eu.glasskube.operator.apps.gitlab

import com.fasterxml.jackson.annotation.JsonIgnore
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
        @field:Required
        override val hostname: String,
        @field:Required
        override val usePathStyle: Boolean
    ) : CloudStorageSpec {
        @field:JsonIgnore
        override val port = null

        @field:JsonIgnore
        override val useSsl = true
    }
}
