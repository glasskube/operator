package eu.glasskube.operator.gitea.dependent

import eu.glasskube.operator.generic.dependent.GeneratedSecret
import eu.glasskube.operator.gitea.Gitea
import eu.glasskube.operator.gitea.GiteaReconciler
import eu.glasskube.operator.gitea.resourceLabels
import eu.glasskube.operator.gitea.secretName
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = GiteaReconciler.SELECTOR)
class GiteaSecret : GeneratedSecret<Gitea>() {
    override val Gitea.generatedSecretName get() = secretName
    override val Gitea.generatedSecretLabels get() = resourceLabels
    override val generatedKeys get() = arrayOf("GITEA__security__SECRET_KEY", "GITEA__metrics__TOKEN")
}
