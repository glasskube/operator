package eu.glasskube.operator.apps.nextcloud

import eu.glasskube.kubernetes.api.model.createEnv
import eu.glasskube.kubernetes.api.model.envVar
import eu.glasskube.kubernetes.api.model.secretKeyRef
import eu.glasskube.operator.Labels
import eu.glasskube.operator.apps.nextcloud.Nextcloud.Postgres.postgresClusterName
import eu.glasskube.operator.apps.nextcloud.Nextcloud.Postgres.postgresDatabaseName
import eu.glasskube.operator.apps.nextcloud.Nextcloud.Postgres.postgresHostName
import eu.glasskube.operator.apps.nextcloud.Nextcloud.Postgres.postgresSecretName
import eu.glasskube.operator.apps.nextcloud.Nextcloud.Redis.redisName
import eu.glasskube.operator.generic.dependent.postgres.PostgresNameMapper
import eu.glasskube.operator.generic.dependent.redis.RedisNameMapper
import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Version

@Group("glasskube.eu")
@Version("v1alpha1")
class Nextcloud : CustomResource<NextcloudSpec, NextcloudStatus>(), Namespaced {
    internal companion object {
        const val APP_NAME = "nextcloud"
        const val APP_VERSION = "27.0.1"
        const val APP_IMAGE = "$APP_NAME:$APP_VERSION-fpm"
        const val NGINX_NAME = "nginx"
        const val NGINX_VERSION = "1.25.1"
        const val NGINX_IMAGE = "$NGINX_NAME:$NGINX_VERSION-alpine"
        const val OFFICE_NAME = "collabora"
        const val OFFICE_VERSION = "23.05.1.2.1"
        const val OFFICE_IMAGE = "$OFFICE_NAME/code:$OFFICE_VERSION"
    }

    object Redis : RedisNameMapper<Nextcloud>() {
        internal const val NAME = "redis"
        private const val VERSION = "7.0"

        override fun getName(primary: Nextcloud) = "${primary.genericResourceName}-$NAME"

        override fun getVersion(primary: Nextcloud) = VERSION

        override fun getLabels(primary: Nextcloud) =
            Labels.resourceLabels(NAME, primary.metadata.name, APP_NAME, VERSION, NAME)

        override fun getLabelSelector(primary: Nextcloud) =
            Labels.resourceLabelSelector(NAME, primary.metadata.name, APP_NAME)
    }

    object Postgres : PostgresNameMapper<Nextcloud>() {
        override fun getName(primary: Nextcloud) = "${primary.genericResourceName}-db"
        override fun getLabels(primary: Nextcloud) = primary.resourceLabels
        override fun getDatabaseName(primary: Nextcloud) = "nextcloud"
    }
}

internal val Nextcloud.resourceLabels
    get() = Labels.resourceLabels(Nextcloud.APP_NAME, metadata.name, Nextcloud.APP_NAME, Nextcloud.APP_VERSION)
internal val Nextcloud.resourceLabelSelector
    get() = Labels.resourceLabelSelector(Nextcloud.APP_NAME, metadata.name, Nextcloud.APP_NAME)
internal val Nextcloud.officeResourceLabels
    get() = Labels.resourceLabels(
        Nextcloud.OFFICE_NAME,
        metadata.name,
        Nextcloud.APP_NAME,
        Nextcloud.OFFICE_VERSION,
        Nextcloud.OFFICE_NAME
    )
internal val Nextcloud.officeResourceLabelSelector
    get() = Labels.resourceLabelSelector(Nextcloud.OFFICE_NAME, metadata.name, Nextcloud.APP_NAME)
internal val Nextcloud.genericResourceName get() = "${Nextcloud.APP_NAME}-${metadata.name}"
internal val Nextcloud.cronName get() = "$genericResourceName-cron"
internal val Nextcloud.volumeName get() = "$genericResourceName-data"
internal val Nextcloud.configName get() = "$genericResourceName-config"
internal val Nextcloud.tlsSecretName get() = "$genericResourceName-tls"
internal val Nextcloud.databaseBackupBucketName get() = "$postgresClusterName-backup"
internal val Nextcloud.officeName get() = "$genericResourceName-${Nextcloud.OFFICE_NAME}"
internal val Nextcloud.officeTlsSecretName get() = "$officeName-tls"

internal val Nextcloud.defaultEnv
    get() = createEnv {
        envVar("REDIS_HOST", redisName)
        envVar("POSTGRES_HOST", postgresHostName)
        envVar("POSTGRES_DB", postgresDatabaseName)
        envVar("NEXTCLOUD_TRUSTED_DOMAINS", spec.host)
    }

internal val Nextcloud.databaseEnv
    get() = createEnv {
        envVar("POSTGRES_USER") { secretKeyRef(postgresSecretName, "username") }
        envVar("POSTGRES_PASSWORD") { secretKeyRef(postgresSecretName, "password") }
    }
