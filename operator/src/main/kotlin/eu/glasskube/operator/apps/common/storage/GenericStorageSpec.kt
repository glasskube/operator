package eu.glasskube.operator.apps.common.storage

import io.fabric8.kubernetes.api.model.Quantity

data class GenericStorageSpec(
    override val size: Quantity?,
    override val storageClassName: String?,
) : StorageSpec
