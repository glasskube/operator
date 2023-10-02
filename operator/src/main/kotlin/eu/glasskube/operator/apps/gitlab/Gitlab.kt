package eu.glasskube.operator.apps.gitlab

import eu.glasskube.kubernetes.client.resources
import eu.glasskube.operator.Labels
import eu.glasskube.operator.generic.dependent.postgres.PostgresNameMapper
import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.client.KubernetesClient
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Version

@Group("glasskube.eu")
@Version("v1alpha1")
class Gitlab : CustomResource<GitlabSpec, GitlabStatus>(), Namespaced {
    companion object {
        const val APP_NAME = "gitlab"
        const val APP_IMAGE = "gitlab/gitlab-ce"
    }

    object Postgres : PostgresNameMapper<Gitlab>() {
        override fun getName(primary: Gitlab) = "${primary.genericResourceName}-db"
        override fun getLabels(primary: Gitlab) = primary.resourceLabels
        override fun getDatabaseName(primary: Gitlab) = "gitlabhq_production"
    }
}

val Gitlab.resourceLabels
    get() = Labels.resourceLabels(Gitlab.APP_NAME, metadata.name, Gitlab.APP_NAME, spec.version)
val Gitlab.resourceLabelSelector get() = Labels.resourceLabelSelector(Gitlab.APP_NAME, metadata.name, Gitlab.APP_NAME)
val Gitlab.genericResourceName: String get() = "${Gitlab.APP_NAME}-${metadata.name}"
val Gitlab.genericRegistryResourceName get() = "$genericResourceName-registry"
val Gitlab.configMapName get() = "$genericResourceName-config"
val Gitlab.volumeName get() = "$genericResourceName-data"
val Gitlab.ingressName get() = genericResourceName
val Gitlab.serviceName get() = genericResourceName
val Gitlab.sshServiceName get() = "$genericResourceName-ssh"
val Gitlab.ingressTlsCertName get() = "$genericResourceName-cert"

fun KubernetesClient.gitlabs() = resources<Gitlab>()
