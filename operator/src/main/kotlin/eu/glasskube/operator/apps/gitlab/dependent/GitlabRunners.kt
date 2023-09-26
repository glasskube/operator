package eu.glasskube.operator.apps.gitlab.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.apps.gitlab.Gitlab
import eu.glasskube.operator.apps.gitlab.GitlabRunnerSpecTemplate
import eu.glasskube.operator.apps.gitlab.resourceLabels
import eu.glasskube.operator.apps.gitlab.runner.GitlabRunner
import eu.glasskube.operator.apps.gitlab.runner.GitlabRunnerSpec
import eu.glasskube.operator.apps.gitlab.runner.gitlabRunner
import eu.glasskube.operator.apps.gitlab.tokenHash
import io.fabric8.kubernetes.api.model.LocalObjectReference
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.BulkDependentResource
import io.javaoperatorsdk.operator.processing.dependent.Matcher
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource

class GitlabRunners :
    CRUDKubernetesDependentResource<GitlabRunner, Gitlab>(GitlabRunner::class.java),
    BulkDependentResource<GitlabRunner, Gitlab> {

    fun desired(primary: Gitlab, template: GitlabRunnerSpecTemplate) = gitlabRunner {
        metadata {
            name = "${primary.metadata.name}-${template.tokenHash}"
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        spec = GitlabRunnerSpec(
            concurrency = template.concurrency,
            token = template.token,
            gitlab = LocalObjectReference(primary.metadata.name)
        ).let { runnerSpec ->
            template.updates?.let { runnerSpec.copy(updates = it) } ?: runnerSpec
        }
    }

    override fun desiredResources(primary: Gitlab, context: Context<Gitlab>) =
        primary.spec.runners
            .map { desired(primary, it) }
            .associateBy { it.metadata.name }

    override fun getSecondaryResources(primary: Gitlab, context: Context<Gitlab>) =
        context.getSecondaryResources(GitlabRunner::class.java)
            .filter { it.metadata.name.startsWith(primary.metadata.name) }
            .associateBy { it.metadata.name }

    override fun match(
        actualResource: GitlabRunner,
        desired: GitlabRunner,
        primary: Gitlab,
        context: Context<Gitlab>
    ): Matcher.Result<GitlabRunner> =
        super<CRUDKubernetesDependentResource>.match(actualResource, desired, primary, context)
}
