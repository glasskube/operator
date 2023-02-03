package eu.glasskube.operator.mariadb

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.api.model.SecretKeySelector
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Version

data class UserMariaDBSpec(
    @JsonProperty("mariaDbRef") var mariaDbRef: DatabasebRef? = null,
    @JsonProperty("passwordSecretKeyRef") var passwordSecretKeyRef: SecretKeySelector? = null,
    @JsonProperty("maxUserConnections") var maxUserConnections: Int = 20
)

@JsonIgnoreProperties(ignoreUnknown = true)
class UserMariaDBStatus

@Group("mariadb.mmontes.io")
@Version("v1alpha1")
class User : CustomResource<UserMariaDBSpec, UserMariaDBStatus>(), Namespaced

inline fun userMariaDB(block: (@MariaDBDslMarker User).() -> Unit) =
    User().apply(block)

inline fun User.spec(block: (@MariaDBDslMarker UserMariaDBSpec).() -> Unit) {
    spec = UserMariaDBSpec().apply(block)
}
