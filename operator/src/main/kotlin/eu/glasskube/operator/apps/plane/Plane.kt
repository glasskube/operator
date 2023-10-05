package eu.glasskube.operator.apps.plane

import com.fasterxml.jackson.annotation.JsonIgnore
import eu.glasskube.operator.Labels
import eu.glasskube.operator.apps.common.backup.ResourceWithBackups
import eu.glasskube.operator.apps.common.database.ResourceWithDatabaseSpec
import eu.glasskube.operator.apps.common.database.postgres.PostgresDatabaseSpec
import eu.glasskube.operator.apps.plane.Plane.Postgres.postgresClusterLabelSelector
import eu.glasskube.operator.generic.dependent.backups.VeleroNameMapper
import eu.glasskube.operator.generic.dependent.postgres.PostgresNameMapper
import eu.glasskube.operator.generic.dependent.redis.RedisNameMapper
import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Plural
import io.fabric8.kubernetes.model.annotation.Version

@Group("glasskube.eu")
@Version("v1alpha1")
@Plural("planes")
class Plane :
    CustomResource<PlaneSpec, PlaneStatus>(),
    Namespaced,
    ResourceWithBackups,
    ResourceWithDatabaseSpec<PostgresDatabaseSpec> {
    object Redis : RedisNameMapper<Plane>() {
        private const val NAME = "redis"
        private const val VERSION = "7.2.1"

        override fun getName(primary: Plane) = "${primary.genericResourceName}-$NAME"

        override fun getLabels(primary: Plane) =
            Labels.resourceLabels(APP_NAME, primary.metadata.name, version = VERSION, component = NAME)

        override fun getLabelSelector(primary: Plane) =
            Labels.resourceLabelSelector(APP_NAME, primary.metadata.name, component = NAME)

        override fun getVersion(primary: Plane) = VERSION
    }

    object Postgres : PostgresNameMapper<Plane>() {
        override fun getName(primary: Plane) = "${primary.genericResourceName}-db"
        override fun getLabels(primary: Plane) =
            Labels.resourceLabels(APP_NAME, primary.metadata.name, component = "database")

        override fun getDatabaseName(primary: Plane) = "plane"
    }

    @delegate:JsonIgnore
    override val velero by lazy {
        object : VeleroNameMapper(this) {
            override val resourceName = genericResourceName
            override val resourceLabels = this@Plane.genericResourceLabels
            override val labelSelectors = listOf(genericResourceLabelSelector, postgresClusterLabelSelector)
        }
    }

    internal companion object {
        const val APP_NAME = "plane"
        const val FRONTEND_NAME = "frontend"
        const val LEGACY_SPACE_NAME = "deploy"
        const val SPACE_NAME = "space"
        const val BACKEND_NAME = "backend"
        const val API_NAME = "api"
        const val WORKER_NAME = "worker"
        const val BEAT_WORKER_NAME = "beat-worker"
    }
}

private val frontendComponentLabel = Labels.COMPONENT to Plane.FRONTEND_NAME
private val spacesComponentLabel = Labels.COMPONENT to Plane.SPACE_NAME
private val apiComponentLabel = Labels.COMPONENT to Plane.API_NAME
private val workerComponentLabel = Labels.COMPONENT to Plane.WORKER_NAME
private val beatWorkerComponentLabel = Labels.COMPONENT to Plane.BEAT_WORKER_NAME

internal val Plane.frontendImage get() = "makeplane/plane-${Plane.FRONTEND_NAME}:${spec.version}"
internal val Plane.spaceImage get() = "makeplane/plane-${Plane.SPACE_NAME}:${spec.version}"
internal val Plane.backendImage get() = "makeplane/plane-${Plane.BACKEND_NAME}:${spec.version}"

internal val Plane.genericResourceName get() = "${Plane.APP_NAME}-${metadata.name}"
internal val Plane.genericResourceLabels
    get() = Labels.resourceLabels(Plane.APP_NAME, metadata.name, version = spec.version)
internal val Plane.genericResourceLabelSelector
    get() = Labels.resourceLabelSelector(Plane.APP_NAME, metadata.name)

internal val Plane.tlsSecretName get() = "$genericResourceName-tls"

internal val Plane.frontendResourceName get() = "$genericResourceName-${Plane.FRONTEND_NAME}"
internal val Plane.frontendResourceLabels get() = genericResourceLabels + frontendComponentLabel
internal val Plane.frontendResourceLabelSelector get() = genericResourceLabelSelector + frontendComponentLabel

internal val Plane.legacySpaceResourceName get() = "$genericResourceName-${Plane.LEGACY_SPACE_NAME}"
internal val Plane.spaceResourceName get() = "$genericResourceName-${Plane.SPACE_NAME}"
internal val Plane.spaceResourceLabels get() = genericResourceLabels + spacesComponentLabel
internal val Plane.spaceResourceLabelSelector get() = genericResourceLabelSelector + spacesComponentLabel

internal val Plane.backendResourceName get() = "$genericResourceName-${Plane.BACKEND_NAME}"

internal val Plane.apiResourceName get() = "$genericResourceName-${Plane.API_NAME}"
internal val Plane.apiResourceLabels get() = genericResourceLabels + apiComponentLabel
internal val Plane.apiResourceLabelSelector get() = genericResourceLabelSelector + apiComponentLabel

internal val Plane.workerResourceName get() = "$genericResourceName-${Plane.WORKER_NAME}"
internal val Plane.workerResourceLabels get() = genericResourceLabels + workerComponentLabel
internal val Plane.workerResourceLabelSelector get() = genericResourceLabelSelector + workerComponentLabel

internal val Plane.beatWorkerResourceName get() = "$genericResourceName-${Plane.BEAT_WORKER_NAME}"
internal val Plane.beatWorkerResourceLabels get() = genericResourceLabels + beatWorkerComponentLabel
internal val Plane.beatWorkerResourceLabelSelector get() = genericResourceLabelSelector + beatWorkerComponentLabel
