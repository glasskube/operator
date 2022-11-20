package eu.glasskube.operator.mariadb

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import eu.glasskube.kubernetes.api.annotation.KubernetesDslMarker
import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Version


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
    @JsonProperty("storageClassName") var storageClassName: String = "standard",
    @JsonProperty("accessModes") var accessModes: Collection<String> = listOf("ReadWriteOnce")
)

data class MariaDBSpec(
    @JsonProperty("rootPasswordSecretKeyRef") var rootPasswordSecretKeyRef: MariaDBPasswordSecretKeyRef,
    @JsonProperty("image") var image: MariaDBImage,
    @JsonProperty("port") var port: Int = 3306,
    @JsonProperty("volumeClaimTemplate") var volumeClaimTemplate: MariaDBVolumeClaimTemplate
)

@JsonIgnoreProperties(ignoreUnknown = true)
class MariaDBStatus

@Group("database.mmontes.io")
@Version("v1alpha1")
class MariaDB : CustomResource<MariaDBSpec, MariaDBStatus>(), Namespaced


inline fun mariaDB(block: (@KubernetesDslMarker MariaDB).() -> Unit) =
    MariaDB().apply(block)

