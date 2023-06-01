package eu.glasskube.operator.infra.mariadb

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Version

data class GrantSpec(
    var mariaDbRef: DatabasebRef? = null,
    var privileges: Collection<String> = listOf("ALL"),
    var database: String,
    var table: String = "*",
    var username: String,
    var grantOption: Boolean = true
)

@JsonIgnoreProperties(ignoreUnknown = true)
class GrantStatus

@Group("mariadb.mmontes.io")
@Version("v1alpha1")
class Grant(var spec: GrantSpec? = null) :
    CustomResource<GrantSpec, GrantStatus>(), Namespaced

inline fun grantMariaDB(block: (@MariaDBDslMarker Grant).() -> Unit) =
    Grant().apply(block)
