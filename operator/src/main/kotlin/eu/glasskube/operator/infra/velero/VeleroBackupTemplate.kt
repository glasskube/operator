package eu.glasskube.operator.infra.velero

import io.fabric8.kubernetes.api.model.LabelSelector
import io.minio.messages.ObjectMetadata

data class VeleroBackupTemplate(
    val metadata: ObjectMetadata? = null,
    val storageLocation: String? = null,
    val ttl: String,
    val csiSnapshotTimeout: String = "10m0s",
    val itemOperationTimeout: String = "1h0m0s",
    val defaultVolumesToFsBackup: Boolean = false,
    val includedNamespaces: List<String>? = null,
    val excludedNamespaces: List<String>? = null,
    val labelSelector: LabelSelector? = null,
    val orLabelSelectors: List<LabelSelector>? = null
)
