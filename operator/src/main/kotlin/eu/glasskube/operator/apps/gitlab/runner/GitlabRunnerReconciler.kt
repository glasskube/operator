package eu.glasskube.operator.apps.gitlab.runner

import eu.glasskube.kubernetes.client.patchOrUpdateStatus
import eu.glasskube.operator.Labels
import eu.glasskube.operator.api.reconciler.getSecondaryResource
import eu.glasskube.operator.apps.gitlab.Gitlab
import eu.glasskube.operator.apps.gitlab.runner.dependent.GitlabRunnerConfigMap
import eu.glasskube.operator.apps.gitlab.runner.dependent.GitlabRunnerDeployment
import eu.glasskube.operator.apps.gitlab.runner.dependent.GitlabRunnerSecret
import eu.glasskube.operator.logger
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration
import io.javaoperatorsdk.operator.api.reconciler.Reconciler
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl
import io.javaoperatorsdk.operator.api.reconciler.dependent.Dependent

@ControllerConfiguration(
    dependents = [
        Dependent(type = GitlabRunnerConfigMap::class),
        Dependent(type = GitlabRunnerSecret::class),
        Dependent(type = GitlabRunnerDeployment::class)
    ]
)
class GitlabRunnerReconciler : Reconciler<GitlabRunner> {
    override fun reconcile(resource: GitlabRunner, context: Context<GitlabRunner>): UpdateControl<GitlabRunner> {
        log.info("Reconciling ${resource.metadata.name}@${resource.metadata.namespace}")
        val readyReplicas = context.getSecondaryResource<Deployment>()
            .map { it.status?.readyReplicas ?: 0 }
            .orElse(0)
        return resource.patchOrUpdateStatus(GitlabRunnerStatus(readyReplicas > 0))
    }

    companion object {
        const val SELECTOR =
            "${Labels.MANAGED_BY_GLASSKUBE},${Labels.PART_OF}=${Gitlab.APP_NAME},${Labels.NAME}=${GitlabRunner.APP_NAME}"

        @JvmStatic
        private val log = logger()
    }
}
