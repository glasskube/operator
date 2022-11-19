package eu.glasskube.operator.mariadb

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import eu.glasskube.kubernetes.api.annotation.KubernetesDslMarker
import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Version
import kotlin.reflect.full.createInstance


data class MariaDBPasswordSecretKeyRef(
    @JsonProperty("name") var name: String,
    @JsonProperty("key") var key: String,
)

data class MariaDBImage(
    @JsonProperty("repository") var repository: String,
    @JsonProperty("tag") var tag: String,
    @JsonProperty("pullPolicy") var pullPolicy: String,
)

data class MariaDBResourcesRequest(
    @JsonProperty("storage") var storage: String
)

data class MariaDBResources(
    @JsonProperty("requests") var requests: MariaDBResourcesRequest
)

data class MariaDBVolumeClaimTemplate(
    @JsonProperty("resources") var resources: MariaDBResources,
    @JsonProperty("storageClassName") var storageClassName: String,
    @JsonProperty("accessModes") var accessModes: Collection<String>
)

data class MariaDBSpec(
    @JsonProperty("rootPasswordSecretKeyRef") var rootPasswordSecretKeyRef: MariaDBPasswordSecretKeyRef,
    @JsonProperty("image") var image: MariaDBImage,
    @JsonProperty("port") var port: Int,
    @JsonProperty("volumeClaimTemplate") var volumeClaimTemplate: MariaDBVolumeClaimTemplate
)

@JsonIgnoreProperties(ignoreUnknown = true)
class MariaDBStatus

@Group("database.mmontes.io")
@Version("v1alpha1")
class MariaDB : CustomResource<MariaDBSpec, MariaDBStatus>(), Namespaced


inline fun mariaDB(block: (@KubernetesDslMarker MariaDB).() -> Unit) =
    MariaDB().apply(block)

@JsonIgnoreProperties(ignoreUnknown = true)
data class DatabaseMariaDbRef(
    @JsonProperty("name") var name: String
)

data class DatabaseMariaDBSpec(
    @JsonProperty("mariaDbRef") var mariaDbRef: DatabaseMariaDbRef,
    @JsonProperty("characterSet") var characterSet: String,
    @JsonProperty("collate") var collate: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
class DatabaseMariaDBStatus


@Group("database.mmontes.io")
@Version("v1alpha1")
class DatabaseMariaDB : CustomResource<DatabaseMariaDBSpec, DatabaseMariaDBStatus>(), Namespaced


inline fun databaseMariaDB(block: (@KubernetesDslMarker DatabaseMariaDB).() -> Unit) =
    DatabaseMariaDB().apply(block)


data class GrantMariaDBSpec(
    @JsonProperty("mariaDbRef") var mariaDbRef: DatabaseMariaDbRef,
    @JsonProperty("privileges") var privileges: Collection<String>,
    @JsonProperty("database") var database: String,
    @JsonProperty("table") var table: String,
    @JsonProperty("username") var username: String,
    @JsonProperty("grantOption") var grantOption: Boolean
)


@JsonIgnoreProperties(ignoreUnknown = true)
class GrantMariaDBStatus


@Group("database.mmontes.io")
@Version("v1alpha1")
class GrantMariaDB : CustomResource<GrantMariaDBSpec, GrantMariaDBStatus>(), Namespaced


inline fun grantMariaDB(block: (@KubernetesDslMarker GrantMariaDB).() -> Unit) =
    GrantMariaDB().apply(block)

inline fun GrantMariaDB.spec(block: (@KubernetesDslMarker GrantMariaDBSpec).() -> Unit) {
    spec = GrantMariaDBSpec::class.createInstance().apply(block)
}


data class UserMariaDBSpec(
    @JsonProperty("mariaDbRef") var mariaDbRef: DatabaseMariaDbRef,
    @JsonProperty("passwordSecretKeyRef") var passwordSecretKeyRef: MariaDBPasswordSecretKeyRef,
    @JsonProperty("maxUserConnections") var maxUserConnections: Int
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
