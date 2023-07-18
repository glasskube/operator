package eu.glasskube.operator.apps.glitchtip.dependent

import eu.glasskube.operator.apps.glitchtip.Glitchtip
import eu.glasskube.operator.apps.glitchtip.GlitchtipReconciler
import eu.glasskube.operator.apps.glitchtip.resourceLabels
import eu.glasskube.operator.apps.glitchtip.secretName
import eu.glasskube.operator.generic.dependent.GeneratedSecret
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = GlitchtipReconciler.SELECTOR)
class GlitchtipSecret : GeneratedSecret<Glitchtip>() {
    override val Glitchtip.generatedSecretName get() = secretName
    override val Glitchtip.generatedSecretLabels get() = resourceLabels
    override val generatedKeys get() = arrayOf("SECRET_KEY")
}
