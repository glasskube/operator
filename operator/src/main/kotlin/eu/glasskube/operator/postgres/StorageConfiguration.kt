package eu.glasskube.operator.postgres

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimSpec

data class StorageConfiguration @JsonCreator constructor(
    @JsonProperty("storageClass")
    val storageClass: String? = null,
    @JsonProperty("size")
    val size: String? = null,
    @JsonProperty("resizeInUseVolumes")
    val resizeInUseVolumes: Boolean? = null,
    @JsonProperty("pvcTemplate")
    val pvcTemplate: PersistentVolumeClaimSpec? = null
)
