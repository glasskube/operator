package eu.glasskube.operator.mariadb

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Version

@JsonIgnoreProperties(ignoreUnknown = true)
data class DatabaseMariaDbRef(
    @JsonProperty("name") var name: String
)

data class DatabaseMariaDBSpec(
    @JsonProperty("mariaDbRef") var mariaDbRef: DatabaseMariaDbRef? = null,
    @JsonProperty("characterSet") var characterSet: String = "utf8",
    @JsonProperty("collate") var collate: String = "utf8_general_ci"
)

@JsonIgnoreProperties(ignoreUnknown = true)
class DatabaseMariaDBStatus

@Group("database.mmontes.io")
@Version("v1alpha1")
class DatabaseMariaDB(var spec: DatabaseMariaDBSpec? = null) :
    CustomResource<DatabaseMariaDBSpec, DatabaseMariaDBStatus>(), Namespaced

inline fun databaseMariaDB(block: (@MariaDBDslMarker DatabaseMariaDB).() -> Unit) =
    DatabaseMariaDB().apply(block)
