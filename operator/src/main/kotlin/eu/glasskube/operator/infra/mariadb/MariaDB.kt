package eu.glasskube.operator.infra.mariadb

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.fabric8.generator.annotation.Nullable
import io.fabric8.kubernetes.api.model.Condition
import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.api.model.ObjectMeta
import io.fabric8.kubernetes.api.model.ResourceRequirements
import io.fabric8.kubernetes.api.model.SecretKeySelector
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Version

data class MariaDBResourcesRequest(
    val storage: String? = null,
    val cpu: String? = null,
    val memory: String? = null
)

data class MariaDBResources(
    val requests: MariaDBResourcesRequest? = null,
    val limits: MariaDBResourcesRequest? = null
)

data class MariaDBVolumeClaimTemplate(
    var resources: MariaDBResources,
    var storageClassName: String = "standard",
    var accessModes: Collection<String> = listOf("ReadWriteOnce")
)

data class MariaDBSpec(
    var rootPasswordSecretKeyRef: SecretKeySelector,
    var image: String,
    var imagePullPolicy: String = "IfNotPresent",
    var database: String?,
    var username: String?,
    var passwordSecretKeyRef: SecretKeySelector?,
    var port: Int = 3306,
    var volumeClaimTemplate: MariaDBVolumeClaimTemplate,
    @field:Nullable
    var resources: ResourceRequirements?,
    var metrics: Metrics? = null,
    var inheritMetadata: ObjectMeta? = null,
    var myCnf: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MariaDBStatus(
    val conditions: List<Condition>?
)

@Group("mariadb.mmontes.io")
@Version("v1alpha1")
class MariaDB : CustomResource<MariaDBSpec, MariaDBStatus>(), Namespaced {
    override fun setSpec(spec: MariaDBSpec?) {
        super.setSpec(spec)
    }
}

inline fun mariaDB(block: (@MariaDBDslMarker MariaDB).() -> Unit) =
    MariaDB().apply(block)

val MariaDB.isReady get() = status?.conditions?.firstOrNull()?.run { type == "Ready" && status == "True" } ?: false
