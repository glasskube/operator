package eu.glasskube.operator.postgres

import io.fabric8.kubernetes.api.model.PersistentVolumeClaimSpec

data class StorageConfiguration(
    var storageClass: String? = null,
    var size: String? = null,
    var resizeInUseVolumes: Boolean? = null,
    var pvcTemplate: PersistentVolumeClaimSpec? = null
)
