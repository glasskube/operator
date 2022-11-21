package eu.glasskube.operator.mariadb

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Version


data class UserMariaDBSpec(
    @JsonProperty("mariaDbRef") var mariaDbRef: DatabaseMariaDbRef? = null,
    @JsonProperty("passwordSecretKeyRef") var passwordSecretKeyRef: MariaDBPasswordSecretKeyRef? = null,
    @JsonProperty("maxUserConnections") var maxUserConnections: Int = 20
)

@JsonIgnoreProperties(ignoreUnknown = true)
class UserMariaDBStatus


@Group("database.mmontes.io")
@Version("v1alpha1")
class UserMariaDB : CustomResource<UserMariaDBSpec, UserMariaDBStatus>(), Namespaced


inline fun userMariaDB(block: (@MariaDBDslMarker UserMariaDB).() -> Unit) =
    UserMariaDB().apply(block)

inline fun UserMariaDB.spec(block: (@MariaDBDslMarker UserMariaDBSpec).() -> Unit) {
    spec = UserMariaDBSpec().apply(block)
}
