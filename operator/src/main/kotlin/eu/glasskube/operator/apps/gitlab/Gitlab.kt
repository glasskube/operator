package eu.glasskube.operator.apps.gitlab

import eu.glasskube.operator.Labels
import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Version

@Group("glasskube.eu")
@Version("v1alpha1")
class Gitlab : CustomResource<GitlabSpec, GitlabStatus>(), Namespaced {
    companion object {
        const val APP_NAME = "gitea"
        const val APP_IMAGE = "gitlab/gitlab-ce"
        const val APP_VERSION = "15.11.6-ce.0"
    }
}

val Gitlab.resourceLabels get() = Labels.resourceLabels(Gitlab.APP_NAME, metadata.name, Gitlab.APP_NAME, Gitlab.APP_VERSION)
val Gitlab.resourceLabelSelector get() = Labels.resourceLabelSelector(Gitlab.APP_NAME, metadata.name, Gitlab.APP_NAME)
val Gitlab.genericResourceName: String get() = metadata.name
val Gitlab.configMapName get() = "$genericResourceName-config"
val Gitlab.databaseName get() = "$genericResourceName-db"
val Gitlab.volumeName get() = "$genericResourceName-data"
val Gitlab.ingressName get() = genericResourceName
val Gitlab.serviceName get() = genericResourceName
val Gitlab.sshServiceName get() = "$genericResourceName-ssh"
