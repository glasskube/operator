package eu.glasskube.operator.postgres

import io.fabric8.kubernetes.api.model.LocalObjectReference

// TODO: Add properties once needed:
//  - certificates
//  - serviceAccountTemplate
//  - affinity
//  - resources
//  - nodeMaintenanceWindow
//  - monitoring
//  - externalClusters
data class ClusterSpec(
    var instances: Int,
    var description: String? = null,
    var inheritedMetadata: EmbeddedObjectMetadata,
    var imageName: String? = null,
    var imagePullPolicy: String? = null,
    var postgresUID: Long? = null,
    var postgresGID: Long? = null,
    var minSyncReplicas: Int? = null,
    var maxSyncReplicas: Int? = null,
    var postgresql: PostgresConfiguration? = null,
    var replicationSlots: ReplicationSlotsConfiguration? = null,
    var bootstrap: BootstrapConfiguration? = null,
    var replica: ReplicaClusterConfiguration? = null,
    var superuserSecret: LocalObjectReference? = null,
    var enableSuperuserAccess: Boolean? = null,
    var imagePullSecrets: LocalObjectReference? = null,
    var storage: StorageConfiguration? = null,
    var walStorage: StorageConfiguration? = null,
    var startDelay: Int? = null,
    var stopDelay: Int? = null,
    var switchoverDelay: Int? = null,
    var primaryUpdateStrategy: PrimaryUpdateStrategy? = null,
    var primaryUpdateMethod: PrimaryUpdateMethod? = null,
    var backup: BackupConfiguration? = null,
    var logLevel: LogLevel? = null
)
