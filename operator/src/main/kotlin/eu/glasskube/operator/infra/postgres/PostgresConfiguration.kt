package eu.glasskube.operator.infra.postgres

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

// TODO: Add properties once needed:
//  - ldap
@JsonInclude(JsonInclude.Include.NON_NULL)
data class PostgresConfiguration(
    val parameters: Map<String, String>? = null,
    @JsonProperty("pg_hba")
    val pgHba: List<String>? = null,
    val syncReplicaElectionConstraint: SyncReplicaElectionConstraints? = null,
    val promotionTimeout: Int? = null,
    val sharedPreloadLibraries: List<String>? = null
)
