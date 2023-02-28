package eu.glasskube.operator.matomo.dependent

import eu.glasskube.operator.generic.dependent.GeneratedSecret
import eu.glasskube.operator.matomo.Matomo
import eu.glasskube.operator.matomo.MatomoReconciler
import eu.glasskube.operator.matomo.resourceLabels
import eu.glasskube.operator.matomo.secretName
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = MatomoReconciler.SELECTOR)
class MatomoSecret : GeneratedSecret<Matomo>() {
    override val Matomo.generatedSecretName get() = secretName
    override val Matomo.generatedSecretLabels get() = resourceLabels
    override val generatedKeys get() = arrayOf("MATOMO_DATABASE_PASSWORD", "ROOT_DATABASE_PASSWORD")
}
