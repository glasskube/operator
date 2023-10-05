package eu.glasskube.operator.apps.gitea.dependent

import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.operator.apps.gitea.Gitea
import eu.glasskube.operator.apps.gitea.GiteaReconciler
import eu.glasskube.operator.apps.gitea.resourceLabels
import eu.glasskube.operator.apps.gitea.secretName
import eu.glasskube.operator.generic.dependent.GeneratedSecret
import io.fabric8.kubernetes.api.model.Secret
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(labelSelector = GiteaReconciler.SELECTOR, resourceDiscriminator = GiteaSecret.Discriminator::class)
class GiteaSecret : GeneratedSecret<Gitea>() {
    internal class Discriminator :
        ResourceIDMatcherDiscriminator<Secret, Gitea>({ ResourceID(it.secretName, it.namespace) })

    override val Gitea.generatedSecretName get() = secretName
    override val Gitea.generatedSecretLabels get() = resourceLabels
    override val generatedKeys get() = arrayOf("GITEA__security__SECRET_KEY", "GITEA__metrics__TOKEN")
}
