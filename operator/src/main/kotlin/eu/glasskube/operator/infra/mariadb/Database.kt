package eu.glasskube.operator.infra.mariadb

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Version

@JsonIgnoreProperties(ignoreUnknown = true)
data class DatabasebRef(
    var name: String
)

data class DatabaseSpec(
    var mariaDbRef: DatabasebRef? = null,
    var characterSet: String = "utf8",
    var collate: String = "utf8_general_ci"
)

@JsonIgnoreProperties(ignoreUnknown = true)
class DatabaseStatus

@Group("mariadb.mmontes.io")
@Version("v1alpha1")
class Database(var spec: DatabaseSpec? = null) :
    CustomResource<DatabaseSpec, DatabaseStatus>(), Namespaced

inline fun databaseMariaDB(block: (@MariaDBDslMarker Database).() -> Unit) =
    Database().apply(block)
