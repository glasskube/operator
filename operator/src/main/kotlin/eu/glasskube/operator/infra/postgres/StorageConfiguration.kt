package eu.glasskube.operator.infra.postgres

import com.fasterxml.jackson.annotation.JsonInclude
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimSpec

@JsonInclude(JsonInclude.Include.NON_NULL)
data class StorageConfiguration(
    val storageClass: String? = null,
    val size: String? = null,
    val resizeInUseVolumes: Boolean? = null,
    val pvcTemplate: PersistentVolumeClaimSpec? = null
)
