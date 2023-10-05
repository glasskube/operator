package eu.glasskube.operator.apps.matomo.dependent

import eu.glasskube.operator.apps.matomo.Matomo
import eu.glasskube.operator.generic.dependent.backups.BackupSpecNotNullCondition
import eu.glasskube.operator.generic.dependent.backups.DependentVeleroSecret
import io.fabric8.kubernetes.api.model.Secret
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(resourceDiscriminator = MatomoVeleroSecret.Discriminator::class)
class MatomoVeleroSecret : DependentVeleroSecret<Matomo>() {
    internal class ReconcilePrecondition : BackupSpecNotNullCondition<Secret, Matomo>()
    internal class Discriminator : DependentVeleroSecret.Discriminator<Matomo>()
}
