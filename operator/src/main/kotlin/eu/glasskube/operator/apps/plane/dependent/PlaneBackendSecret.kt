package eu.glasskube.operator.apps.plane.dependent

import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.operator.apps.plane.Plane
import eu.glasskube.operator.apps.plane.backendResourceName
import eu.glasskube.operator.apps.plane.genericResourceLabels
import eu.glasskube.operator.generic.dependent.GeneratedSecret
import io.fabric8.kubernetes.api.model.Secret
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(resourceDiscriminator = PlaneBackendSecret.Discriminator::class)
class PlaneBackendSecret : GeneratedSecret<Plane>() {
    internal class Discriminator :
        ResourceIDMatcherDiscriminator<Secret, Plane>({ ResourceID(it.backendResourceName, it.namespace) })

    override val Plane.generatedSecretName get() = backendResourceName
    override val Plane.generatedSecretLabels get() = genericResourceLabels
    override val generatedKeys = arrayOf("SECRET_KEY")
}
