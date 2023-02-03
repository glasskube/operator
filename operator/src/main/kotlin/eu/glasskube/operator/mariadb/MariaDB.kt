package eu.glasskube.operator.mariadb

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.api.model.SecretKeySelector
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Version

data class MariaDBImage(
    @JsonProperty("repository")
    var repository: String,
    @JsonProperty("tag")
    var tag: String,
    @JsonProperty("pullPolicy")
    var pullPolicy: String? = null
)

data class MariaDBResourcesRequest @JsonCreator constructor(
    @JsonProperty("storage")
    val storage: String? = null,
    @JsonProperty("cpu")
    val cpu: String? = null,
    @JsonProperty("memory")
    val memory: String? = null
)

data class MariaDBResources @JsonCreator constructor(
    @JsonProperty("requests")
    val requests: MariaDBResourcesRequest? = null,
    @JsonProperty("limits")
    val limits: MariaDBResourcesRequest? = null,
)

data class MariaDBVolumeClaimTemplate(
    @JsonProperty("resources") var resources: MariaDBResources,
    @JsonProperty("storageClassName") var storageClassName: String = "standard",
    @JsonProperty("accessModes") var accessModes: Collection<String> = listOf("ReadWriteOnce")
)

data class MariaDBSpec(
    @JsonProperty("rootPasswordSecretKeyRef")
    var rootPasswordSecretKeyRef: SecretKeySelector,
    @JsonProperty("image")
    var image: MariaDBImage,
    @JsonProperty("port")
    var port: Int = 3306,
    @JsonProperty("volumeClaimTemplate")
    var volumeClaimTemplate: MariaDBVolumeClaimTemplate,
    @JsonProperty("metrics")
    var metrics: Metrics? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
class MariaDBStatus

@Group("mariadb.mmontes.io")
@Version("v1alpha1")
class MariaDB : CustomResource<MariaDBSpec, MariaDBStatus>(), Namespaced {
    override fun setSpec(spec: MariaDBSpec?) {
        super.setSpec(spec)
    }
}

inline fun mariaDB(block: (@MariaDBDslMarker MariaDB).() -> Unit) =
    MariaDB().apply(block)
