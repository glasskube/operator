package eu.glasskube.operator.postgres

import io.fabric8.kubernetes.api.model.PersistentVolumeClaimSpec

data class StorageConfiguration(
    val storageClass: String? = null,
    val size: String? = null,
    val resizeInUseVolumes: Boolean? = null,
    val pvcTemplate: PersistentVolumeClaimSpec? = null
)
