package eu.glasskube.operator.apps.gitea

import eu.glasskube.operator.Labels
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
class Gitea : CustomResource<GiteaSpec, GiteaStatus>(), Namespaced {
    companion object {
        const val APP_NAME = "gitea"
        const val APP_VERSION = "1.20.4"
        const val WORK_DIR = "/data"
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
}

val Gitea.resourceLabels
    get() = Labels.resourceLabels(Gitea.APP_NAME, metadata.name, Gitea.APP_NAME, Gitea.APP_VERSION)
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
