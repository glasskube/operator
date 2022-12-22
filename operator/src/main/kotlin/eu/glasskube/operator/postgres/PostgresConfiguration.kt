package eu.glasskube.operator.postgres

import com.fasterxml.jackson.annotation.JsonProperty

// TODO: Add properties once needed:
//  - ldap
data class PostgresConfiguration(
    var parameters: Map<String, String>? = null,
    @JsonProperty("pg_hba")
    var pgHba: List<String>? = null,
    var syncReplicaElectionConstraint: SyncReplicaElectionConstraints? = null,
    var promotionTimeout: Int? = null,
    var sharedPreloadLibraries: List<String>? = null
)
