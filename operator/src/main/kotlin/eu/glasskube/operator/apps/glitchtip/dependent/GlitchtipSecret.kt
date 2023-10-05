package eu.glasskube.operator.apps.glitchtip.dependent

import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.operator.apps.glitchtip.Glitchtip
import eu.glasskube.operator.apps.glitchtip.GlitchtipReconciler
import eu.glasskube.operator.apps.glitchtip.resourceLabels
import eu.glasskube.operator.apps.glitchtip.secretName
import eu.glasskube.operator.generic.dependent.GeneratedSecret
import io.fabric8.kubernetes.api.model.Secret
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(
    labelSelector = GlitchtipReconciler.SELECTOR,
    resourceDiscriminator = GlitchtipSecret.Discriminator::class
)
class GlitchtipSecret : GeneratedSecret<Glitchtip>() {
    internal class Discriminator : ResourceIDMatcherDiscriminator<Secret, Glitchtip>({ ResourceID(it.secretName, it.namespace) })

    override val Glitchtip.generatedSecretName get() = secretName
    override val Glitchtip.generatedSecretLabels get() = resourceLabels
    override val generatedKeys get() = arrayOf("SECRET_KEY")
}
