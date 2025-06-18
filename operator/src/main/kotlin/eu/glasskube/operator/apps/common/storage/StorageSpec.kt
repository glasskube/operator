package eu.glasskube.operator.apps.common.storage

import io.fabric8.kubernetes.api.model.Quantity

interface StorageSpec {
    val size: Quantity?
    val storageClassName: String?
}
