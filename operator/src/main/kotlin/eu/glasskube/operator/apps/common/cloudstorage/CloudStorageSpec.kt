package eu.glasskube.operator.apps.common.cloudstorage

import com.fasterxml.jackson.annotation.JsonIgnore
import io.fabric8.kubernetes.api.model.SecretKeySelector

interface CloudStorageSpec {
    val bucket: String
    val accessKeySecret: SecretKeySelector
    val secretKeySecret: SecretKeySelector
    val region: String?
    val hostname: String?
    val port: Int?
    val useSsl: Boolean
    val usePathStyle: Boolean?

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
