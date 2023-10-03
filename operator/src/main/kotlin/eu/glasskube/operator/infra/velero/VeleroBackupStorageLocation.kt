package eu.glasskube.operator.infra.velero

import com.fasterxml.jackson.annotation.JsonProperty
import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.api.model.SecretKeySelector
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Kind
import io.fabric8.kubernetes.model.annotation.Version

@Kind("BackupStorageLocation")
@Group("velero.io")
@Version("v1")
class VeleroBackupStorageLocation :
    CustomResource<VeleroBackupStorageLocation.Spec, VeleroBackupStorageLocation.Status>(), Namespaced {

    data class Spec(
        val objectStorage: ObjectStorage,
        val credential: SecretKeySelector?,
        val config: Config
    ) {
        val accessMode = "ReadWrite"
        val default = false
        val provider = "aws"

        data class ObjectStorage(
            val bucket: String
        )

        data class Config(
            val region: String?,
            val s3Url: String?,
            val s3ForcePathStyle: S3ForcePathStyle
        ) {
            enum class S3ForcePathStyle {
                @JsonProperty("true")
                TRUE,

                @JsonProperty("false")
                FALSE;

                companion object {
                    fun from(value: Boolean) = when (value) {
                        true -> TRUE
                        false -> FALSE
                    }
                }
            }
        }
    }

    data class Status(
        val phase: String?
    )
}

fun veleroBackupStorageLocation(block: VeleroBackupStorageLocation.() -> Unit) =
    VeleroBackupStorageLocation().apply(block)
