package eu.glasskube.operator.apps.metabase.dependent

import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.operator.apps.metabase.Metabase
import eu.glasskube.operator.apps.metabase.MetabaseReconciler
import eu.glasskube.operator.apps.metabase.resourceLabels
import eu.glasskube.operator.apps.metabase.secretName
import eu.glasskube.operator.generic.dependent.GeneratedSecret
import io.fabric8.kubernetes.api.model.Secret
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(
    labelSelector = MetabaseReconciler.SELECTOR,
    resourceDiscriminator = MetabaseSecret.Discriminator::class
)
class MetabaseSecret : GeneratedSecret<Metabase>() {
    internal class Discriminator :
        ResourceIDMatcherDiscriminator<Secret, Metabase>({ ResourceID(it.secretName, it.namespace) })

    override val Metabase.generatedSecretName get() = secretName
    override val Metabase.generatedSecretLabels get() = resourceLabels
    override val generatedKeys get() = arrayOf("MB_ENCRYPTION_SECRET_KEY")
}
