package eu.glasskube.operator.apps.common.backup

import com.fasterxml.jackson.annotation.JsonIgnore
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
        val hostname: String?,
        @field:Nullable
        val port: Int?,
        val useSsl: Boolean = true,
        @field:Nullable
        val region: String?,
        @field:Required
        val bucket: String,
        @field:Required
        val accessKeySecret: SecretKeySelector,
        @field:Required
        val secretKeySecret: SecretKeySelector,
        val usePathStyle: Boolean = true
    ) {
        @get:JsonIgnore
        val endpoint
            get() = hostname?.let {
                buildString {
                    append(if (useSsl) "https" else "http", "://", it)
                    if (port != null) {
                        append(":", port)
                    }
                }
            }
    }
}
