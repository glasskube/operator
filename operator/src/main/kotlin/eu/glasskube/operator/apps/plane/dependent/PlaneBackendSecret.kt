package eu.glasskube.operator.apps.plane.dependent

import eu.glasskube.operator.apps.plane.Plane
import eu.glasskube.operator.apps.plane.backendResourceName
import eu.glasskube.operator.apps.plane.genericResourceLabels
import eu.glasskube.operator.generic.dependent.GeneratedSecret
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent
class PlaneBackendSecret : GeneratedSecret<Plane>() {
    override val Plane.generatedSecretName get() = backendResourceName
    override val Plane.generatedSecretLabels get() = genericResourceLabels
    override val generatedKeys = arrayOf("SECRET_KEY")
}
