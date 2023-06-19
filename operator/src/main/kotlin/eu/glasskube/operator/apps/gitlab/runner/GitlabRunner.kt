package eu.glasskube.operator.apps.gitlab.runner

import eu.glasskube.operator.Labels
import eu.glasskube.operator.apps.gitlab.Gitlab
import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Version

@Group("glasskube.eu")
@Version("v1alpha1")
class GitlabRunner : CustomResource<GitlabRunnerSpec, GitlabRunnerStatus>(), Namespaced {
    companion object {
        const val APP_NAME = "gitlab-runner"
        const val APP_IMAGE = "gitlab/gitlab-runner"
        const val APP_VERSION = "v16.0.2"
        const val DIND_NAME = "dind"
        const val DIND_IMAGE = "docker"
        const val DIND_VERSION = "20.10.12-dind"
    }
}

inline fun gitlabRunner(block: GitlabRunner.() -> Unit) = GitlabRunner().apply(block)

val GitlabRunner.resourceLabels
    get() = Labels.resourceLabels(
        GitlabRunner.APP_NAME,
        metadata.name,
        Gitlab.APP_NAME,
        GitlabRunner.APP_VERSION,
        GitlabRunner.APP_NAME
    )
val GitlabRunner.resourceLabelSelector
    get() = Labels.resourceLabelSelector(GitlabRunner.APP_NAME, metadata.name, Gitlab.APP_NAME)
val GitlabRunner.genericResourceName get() = "${GitlabRunner.APP_NAME}-${metadata.name}"
val GitlabRunner.configMapName get() = "$genericResourceName-template"
val GitlabRunner.secretName get() = "$genericResourceName-token"
