package eu.glasskube.operator.mariadb

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Version

data class GrantMariaDBSpec(
    @JsonProperty("mariaDbRef") var mariaDbRef: DatabaseMariaDbRef? = null,
    @JsonProperty("privileges") var privileges: Collection<String> = listOf("ALL"),
    @JsonProperty("database") var database: String = "",
    @JsonProperty("table") var table: String = "*",
    @JsonProperty("username") var username: String = "",
    @JsonProperty("grantOption") var grantOption: Boolean = true
)

@JsonIgnoreProperties(ignoreUnknown = true)
class GrantMariaDBStatus

@Group("database.mmontes.io")
@Version("v1alpha1")
class GrantMariaDB(var spec: GrantMariaDBSpec? = null) :
    CustomResource<GrantMariaDBSpec, GrantMariaDBStatus>(), Namespaced

inline fun grantMariaDB(block: (@MariaDBDslMarker GrantMariaDB).() -> Unit) =
    GrantMariaDB().apply(block)
