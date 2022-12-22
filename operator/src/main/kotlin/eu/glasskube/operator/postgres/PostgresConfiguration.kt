package eu.glasskube.operator.postgres

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

// TODO: Add properties once needed:
//  - ldap
data class PostgresConfiguration @JsonCreator constructor(
    @JsonProperty("parameters")
    val parameters: Map<String, String>? = null,
    @JsonProperty("pg_hba")
    val pgHba: List<String>? = null,
    @JsonProperty("syncReplicaElectionConstraint")
    val syncReplicaElectionConstraint: SyncReplicaElectionConstraints? = null,
    @JsonProperty("promotionTimeout")
    val promotionTimeout: Int? = null,
    @JsonProperty("sharedPreloadLibraries")
    val sharedPreloadLibraries: List<String>? = null
)
