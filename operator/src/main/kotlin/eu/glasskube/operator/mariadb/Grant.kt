package eu.glasskube.operator.mariadb

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Version

data class GrantSpec(
    @JsonProperty("mariaDbRef") var mariaDbRef: DatabasebRef? = null,
    @JsonProperty("privileges") var privileges: Collection<String> = listOf("ALL"),
    @JsonProperty("database") var database: String = "",
    @JsonProperty("table") var table: String = "*",
    @JsonProperty("username") var username: String = "",
    @JsonProperty("grantOption") var grantOption: Boolean = true
)

@JsonIgnoreProperties(ignoreUnknown = true)
class GrantStatus

@Group("mariadb.mmontes.io")
@Version("v1alpha1")
class Grant(var spec: GrantSpec? = null) :
    CustomResource<GrantSpec, GrantStatus>(), Namespaced

inline fun grantMariaDB(block: (@MariaDBDslMarker Grant).() -> Unit) =
    Grant().apply(block)
