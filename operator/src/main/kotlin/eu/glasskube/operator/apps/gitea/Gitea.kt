package eu.glasskube.operator.apps.gitea

import eu.glasskube.operator.Labels
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
        const val APP_VERSION = "1.19.3"
        const val REDIS_NAME = "redis"
        const val REDIS_VERSION = "7.0"
        const val WORK_DIR = "/data"
    }
}

val Gitea.resourceLabels
    get() = Labels.resourceLabels(Gitea.APP_NAME, metadata.name, Gitea.APP_NAME, Gitea.APP_VERSION)
val Gitea.resourceLabelSelector
    get() = Labels.resourceLabelSelector(Gitea.APP_NAME, metadata.name, Gitea.APP_NAME)
val Gitea.redisLabels
    get() = Labels.resourceLabels(Gitea.REDIS_NAME, redisName, Gitea.APP_NAME)
val Gitea.redisLabelSelector
    get() = Labels.resourceLabelSelector(Gitea.REDIS_NAME, redisName, Gitea.APP_NAME)
val Gitea.genericResourceName get() = "${Gitea.APP_NAME}-${metadata.name}"
val Gitea.deploymentName get() = genericResourceName
val Gitea.secretName get() = genericResourceName
val Gitea.configMapName get() = genericResourceName
val Gitea.iniConfigMapName get() = "$genericResourceName-ini"
val Gitea.dbClusterName get() = "$genericResourceName-db"
val Gitea.redisName get() = "$genericResourceName-redis"
val Gitea.httpServiceName get() = "$genericResourceName-http"
val Gitea.sshServiceName get() = "$genericResourceName-ssh"
val Gitea.ingressTlsCertName get() = "$genericResourceName-cert"
