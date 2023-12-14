package eu.glasskube.operator.apps.gitea

import com.fasterxml.jackson.annotation.JsonIgnore
import eu.glasskube.operator.Labels
import eu.glasskube.operator.apps.common.backup.ResourceWithBackups
import eu.glasskube.operator.apps.common.database.ResourceWithDatabaseSpec
import eu.glasskube.operator.apps.common.database.postgres.PostgresDatabaseSpec
import eu.glasskube.operator.apps.gitea.Gitea.Postgres.postgresClusterLabelSelector
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
@Plural("giteas")
class Gitea :
    CustomResource<GiteaSpec, GiteaStatus>(),
    Namespaced,
    ResourceWithBackups,
    ResourceWithDatabaseSpec<PostgresDatabaseSpec> {

    companion object {
        const val APP_NAME = "gitea"
    }

    object Redis : RedisNameMapper<Gitea>() {
        const val NAME = "redis"
        private const val VERSION = "7.0"

        override fun getName(primary: Gitea) = "${primary.genericResourceName}-$NAME"

        override fun getVersion(primary: Gitea) = VERSION

        override fun getLabels(primary: Gitea) =
            Labels.resourceLabels(NAME, getName(primary), APP_NAME)

        override fun getLabelSelector(primary: Gitea) =
            Labels.resourceLabelSelector(NAME, getName(primary), APP_NAME)
    }

    object Postgres : PostgresNameMapper<Gitea>() {
        override fun getName(primary: Gitea) = "${primary.genericResourceName}-db"
        override fun getLabels(primary: Gitea) = primary.resourceLabels
        override fun getDatabaseName(primary: Gitea) = "gitea"
    }

    object Runner {
        internal const val APP_NAME = "act-runner"
        internal const val APP_VERSION = "0.2.6"
        internal const val APP_IMAGE = "${Gitea.APP_NAME}/act_runner:$APP_VERSION"
        internal const val DOCKER_IMAGE = "docker:23.0.6-dind"
    }

    @delegate:JsonIgnore
    override val velero by lazy {
        object : VeleroNameMapper(this) {
            override val resourceName = genericResourceName
            override val resourceLabels = this@Gitea.resourceLabels
            override val labelSelectors = listOf(resourceLabelSelector, postgresClusterLabelSelector)
        }
    }
}

internal const val GITEA_RUNNER_LABEL = "glasskube.eu/gitea-runner"

val Gitea.resourceLabels
    get() = Labels.resourceLabels(Gitea.APP_NAME, metadata.name, Gitea.APP_NAME, spec.version)
val Gitea.resourceLabelSelector
    get() = Labels.resourceLabelSelector(Gitea.APP_NAME, metadata.name, Gitea.APP_NAME)
val Gitea.genericResourceName get() = "${Gitea.APP_NAME}-${metadata.name}"
val Gitea.deploymentName get() = genericResourceName
val Gitea.secretName get() = genericResourceName
val Gitea.configMapName get() = genericResourceName
val Gitea.iniConfigMapName get() = "$genericResourceName-ini"
val Gitea.httpServiceName get() = "$genericResourceName-http"
val Gitea.sshServiceName get() = "$genericResourceName-ssh"
val Gitea.ingressTlsCertName get() = "$genericResourceName-cert"
fun Gitea.getRunnerName(runner: GiteaActionRunnerSpecTemplate) = "$genericResourceName-runner-${runner.tokenHash}"
val GiteaActionRunnerSpecTemplate.resourceLabels
    get() = mapOf(Labels.COMPONENT to Gitea.Runner.APP_NAME, GITEA_RUNNER_LABEL to tokenHash)
