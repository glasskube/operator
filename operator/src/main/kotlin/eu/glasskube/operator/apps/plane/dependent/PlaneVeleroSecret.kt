package eu.glasskube.operator.apps.plane.dependent

import eu.glasskube.operator.apps.plane.Plane
import eu.glasskube.operator.generic.dependent.backups.BackupSpecNotNullCondition
import eu.glasskube.operator.generic.dependent.backups.DependentVeleroSecret
import io.fabric8.kubernetes.api.model.Secret
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(resourceDiscriminator = PlaneVeleroSecret.Discriminator::class)
class PlaneVeleroSecret : DependentVeleroSecret<Plane>() {
    internal class ReconcilePrecondition : BackupSpecNotNullCondition<Secret, Plane>()
    internal class Discriminator : DependentVeleroSecret.Discriminator<Plane>()
}
