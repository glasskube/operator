package eu.glasskube.operator.apps.gitlab.runner.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.secret
import eu.glasskube.operator.apps.gitlab.runner.GitlabRunner
import eu.glasskube.operator.apps.gitlab.runner.GitlabRunnerReconciler
import eu.glasskube.operator.apps.gitlab.runner.resourceLabels
import eu.glasskube.operator.apps.gitlab.runner.secretName
import eu.glasskube.operator.encodeBase64
import io.fabric8.kubernetes.api.model.Secret
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = GitlabRunnerReconciler.SELECTOR)
class GitlabRunnerSecret : CRUDKubernetesDependentResource<Secret, GitlabRunner>(Secret::class.java) {
    override fun desired(primary: GitlabRunner, context: Context<GitlabRunner>) = secret {
        metadata {
            name = primary.secretName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        data = mapOf(
            "REGISTRATION_TOKEN" to primary.spec.token.encodeBase64()
        )
    }
}
