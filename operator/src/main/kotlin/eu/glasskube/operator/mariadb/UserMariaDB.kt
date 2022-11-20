package eu.glasskube.operator.mariadb

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import eu.glasskube.kubernetes.api.annotation.KubernetesDslMarker
import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Version
import kotlin.reflect.full.createInstance


data class UserMariaDBSpec(
    @JsonProperty("mariaDbRef") var mariaDbRef: DatabaseMariaDbRef,
    @JsonProperty("passwordSecretKeyRef") var passwordSecretKeyRef: MariaDBPasswordSecretKeyRef,
    @JsonProperty("maxUserConnections") var maxUserConnections: Int = 20
)

@JsonIgnoreProperties(ignoreUnknown = true)
class UserMariaDBStatus


@Group("database.mmontes.io")
@Version("v1alpha1")
class UserMariaDB : CustomResource<UserMariaDBSpec, UserMariaDBStatus>(), Namespaced


inline fun userMariaDB(block: (@KubernetesDslMarker UserMariaDB).() -> Unit) =
    UserMariaDB().apply(block)

inline fun UserMariaDB.spec(block: (@KubernetesDslMarker UserMariaDBSpec).() -> Unit) {
    spec = UserMariaDBSpec::class.createInstance().apply(block)
}
