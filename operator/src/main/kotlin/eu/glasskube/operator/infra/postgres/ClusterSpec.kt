package eu.glasskube.operator.infra.postgres

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.fabric8.kubernetes.api.model.LocalObjectReference

// TODO: Add properties once needed:
//  - certificates
//  - serviceAccountTemplate
//  - affinity
//  - resources
//  - nodeMaintenanceWindow
//  - externalClusters
@JsonIgnoreProperties(ignoreUnknown = true)
data class ClusterSpec(
    val instances: Int,
    val description: String? = null,
    val inheritedMetadata: EmbeddedObjectMetadata? = null,
    val imageName: String? = null,
    val imagePullPolicy: String? = null,
    val postgresUID: Long? = null,
    val postgresGID: Long? = null,
    val minSyncReplicas: Int? = null,
    val maxSyncReplicas: Int? = null,
    val postgresql: PostgresConfiguration? = null,
    val replicationSlots: ReplicationSlotsConfiguration? = null,
    val bootstrap: BootstrapConfiguration? = null,
    val replica: ReplicaClusterConfiguration? = null,
    val superuserSecret: LocalObjectReference? = null,
    val enableSuperuserAccess: Boolean? = null,
    val imagePullSecrets: LocalObjectReference? = null,
    val storage: StorageConfiguration? = null,
    val walStorage: StorageConfiguration? = null,
    val startDelay: Int? = null,
    val stopDelay: Int? = null,
    val switchoverDelay: Int? = null,
    val primaryUpdateStrategy: PrimaryUpdateStrategy? = null,
    val primaryUpdateMethod: PrimaryUpdateMethod? = null,
    val backup: BackupConfiguration? = null,
    val logLevel: LogLevel? = null,
    val monitoring: MonitoringConfiguration? = null
)
