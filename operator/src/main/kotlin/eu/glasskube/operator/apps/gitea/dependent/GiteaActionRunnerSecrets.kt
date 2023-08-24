package eu.glasskube.operator.apps.gitea.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.kubernetes.api.model.secret
import eu.glasskube.operator.apps.gitea.GITEA_RUNNER_LABEL
import eu.glasskube.operator.apps.gitea.Gitea
import eu.glasskube.operator.apps.gitea.GiteaReconciler
import eu.glasskube.operator.apps.gitea.getRunnerName
import eu.glasskube.operator.apps.gitea.resourceLabels
import eu.glasskube.utils.encodeBase64
import io.fabric8.kubernetes.api.model.Secret
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.BulkDependentResource
import io.javaoperatorsdk.operator.processing.dependent.Matcher
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = GiteaReconciler.SELECTOR)
class GiteaActionRunnerSecrets :
    CRUDKubernetesDependentResource<Secret, Gitea>(Secret::class.java), BulkDependentResource<Secret, Gitea> {
    override fun desiredResources(primary: Gitea, context: Context<Gitea>) =
        primary.spec.actions.runners
            .map {
                secret {
                    metadata {
                        name(primary.getRunnerName(it))
                        namespace(primary.namespace)
                        labels(primary.resourceLabels + it.resourceLabels)
                    }
                    data = mapOf("GITEA_RUNNER_REGISTRATION_TOKEN" to it.token.encodeBase64())
                }
            }
            .associateBy { it.metadata.name }

    override fun getSecondaryResources(primary: Gitea, context: Context<Gitea>) =
        context.getSecondaryResources(Secret::class.java)
            .filter { GITEA_RUNNER_LABEL in it.metadata.labels }
            .associateBy { it.metadata.name }
            .toMutableMap()

    override fun match(
        actualResource: Secret,
        desired: Secret,
        primary: Gitea,
        context: Context<Gitea>
    ): Matcher.Result<Secret> =
        super<CRUDKubernetesDependentResource>.match(actualResource, desired, primary, context)
}
