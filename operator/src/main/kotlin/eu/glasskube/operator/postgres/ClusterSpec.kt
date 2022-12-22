package eu.glasskube.operator.postgres

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.fabric8.kubernetes.api.model.LocalObjectReference

// TODO: Add properties once needed:
//  - certificates
//  - serviceAccountTemplate
//  - affinity
//  - resources
//  - nodeMaintenanceWindow
//  - monitoring
//  - externalClusters
@JsonIgnoreProperties(ignoreUnknown = true)
data class ClusterSpec @JsonCreator constructor(
    @JsonProperty("instances")
    val instances: Int,
    @JsonProperty("description")
    val description: String? = null,
    @JsonProperty("inheritedMetadata")
    val inheritedMetadata: EmbeddedObjectMetadata? = null,
    @JsonProperty("imageName")
    val imageName: String? = null,
    @JsonProperty("imagePullPolicy")
    val imagePullPolicy: String? = null,
    @JsonProperty("postgresUID")
    val postgresUID: Long? = null,
    @JsonProperty("postgresGID")
    val postgresGID: Long? = null,
    @JsonProperty("minSyncReplicas")
    val minSyncReplicas: Int? = null,
    @JsonProperty("maxSyncReplicas")
    val maxSyncReplicas: Int? = null,
    @JsonProperty("postgresql")
    val postgresql: PostgresConfiguration? = null,
    @JsonProperty("replicationSlots")
    val replicationSlots: ReplicationSlotsConfiguration? = null,
    @JsonProperty("bootstrap")
    val bootstrap: BootstrapConfiguration? = null,
    @JsonProperty("replica")
    val replica: ReplicaClusterConfiguration? = null,
    @JsonProperty("superuserSecret")
    val superuserSecret: LocalObjectReference? = null,
    @JsonProperty("enableSuperuserAccess")
    val enableSuperuserAccess: Boolean? = null,
    @JsonProperty("imagePullSecrets")
    val imagePullSecrets: LocalObjectReference? = null,
    @JsonProperty("storage")
    val storage: StorageConfiguration? = null,
    @JsonProperty("walStorage")
    val walStorage: StorageConfiguration? = null,
    @JsonProperty("startDelay")
    val startDelay: Int? = null,
    @JsonProperty("stopDelay")
    val stopDelay: Int? = null,
    @JsonProperty("switchoverDelay")
    val switchoverDelay: Int? = null,
    @JsonProperty("primaryUpdateStrategy")
    val primaryUpdateStrategy: PrimaryUpdateStrategy? = null,
    @JsonProperty("primaryUpdateMethod")
    val primaryUpdateMethod: PrimaryUpdateMethod? = null,
    @JsonProperty("backup")
    val backup: BackupConfiguration? = null,
    @JsonProperty("logLevel")
    val logLevel: LogLevel? = null
)
