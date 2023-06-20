package eu.glasskube.operator.apps.matomo.dependent

import eu.glasskube.operator.apps.matomo.Matomo
import eu.glasskube.operator.apps.matomo.MatomoReconciler
import eu.glasskube.operator.apps.matomo.databaseSecretName
import eu.glasskube.operator.apps.matomo.resourceLabels
import eu.glasskube.operator.generic.dependent.GeneratedSecret
import io.fabric8.kubernetes.api.model.Secret
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(
    labelSelector = MatomoReconciler.SELECTOR,
    resourceDiscriminator = MatomoDatabaseSecret.Discriminator::class
)
class MatomoDatabaseSecret : GeneratedSecret<Matomo>() {
    class Discriminator : ResourceIDMatcherDiscriminator<Secret, Matomo>({ ResourceID(it.databaseSecretName) })

    override val Matomo.generatedSecretName get() = databaseSecretName
    override val Matomo.generatedSecretLabels get() = resourceLabels
    override val generatedKeys get() = arrayOf("MATOMO_DATABASE_PASSWORD", "ROOT_DATABASE_PASSWORD")
}
