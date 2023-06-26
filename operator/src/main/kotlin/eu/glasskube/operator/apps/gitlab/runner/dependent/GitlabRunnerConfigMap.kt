package eu.glasskube.operator.apps.gitlab.runner.dependent

import eu.glasskube.kubernetes.api.model.configMap
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.api.reconciler.getSecondaryResource
import eu.glasskube.operator.apps.gitlab.Gitlab
import eu.glasskube.operator.apps.gitlab.gitlabs
import eu.glasskube.operator.apps.gitlab.runner.GitlabRunner
import eu.glasskube.operator.apps.gitlab.runner.GitlabRunnerReconciler
import eu.glasskube.operator.apps.gitlab.runner.configMapName
import eu.glasskube.operator.apps.gitlab.runner.resourceLabels
import eu.glasskube.operator.apps.gitlab.serviceName
import eu.glasskube.operator.logger
import io.fabric8.kubernetes.api.model.ConfigMap
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = GitlabRunnerReconciler.SELECTOR)
class GitlabRunnerConfigMap : CRUDKubernetesDependentResource<ConfigMap, GitlabRunner>(ConfigMap::class.java) {
    override fun desired(primary: GitlabRunner, context: Context<GitlabRunner>) = configMap {
        metadata {
            name = primary.configMapName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }

        data = mapOf(
            GitlabRunnerDeployment.CONFIG_TEMPLATE_NAME to with(primary.parent) {
                """
                    [[runners]]
                    url = "http://$serviceName"
                    clone_url = "http://$serviceName"
                    executor = "docker"
                    concurrent = ${primary.spec.concurrency}

                    [runners.docker]
                    image = "ubuntu:23.10"
                """.trimIndent()
            }
        )
    }

    override fun onUpdated(
        primary: GitlabRunner,
        updated: ConfigMap,
        actual: ConfigMap,
        context: Context<GitlabRunner>
    ) {
        super.onUpdated(primary, updated, actual, context)
        context.getSecondaryResource<Deployment>().ifPresent {
            log.info("Restarting deployment after config change")
            kubernetesClient.apps().deployments().resource(it).rolling().restart()
        }
    }

    private val GitlabRunner.parent: Gitlab
        get() = kubernetesClient.gitlabs().inNamespace(metadata.namespace).withName(spec.gitlab.name).require()

    companion object {
        @JvmStatic
        private val log = logger()
    }
}
