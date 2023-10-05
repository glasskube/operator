package eu.glasskube.operator.apps.gitea.dependent

import eu.glasskube.operator.apps.gitea.Gitea
import eu.glasskube.operator.generic.dependent.backups.BackupSpecNotNullCondition
import eu.glasskube.operator.generic.dependent.backups.DependentVeleroSecret
import io.fabric8.kubernetes.api.model.Secret
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(resourceDiscriminator = GiteaVeleroSecret.Discriminator::class)
class GiteaVeleroSecret : DependentVeleroSecret<Gitea>() {
    internal class ReconcilePrecondition : BackupSpecNotNullCondition<Secret, Gitea>()
    internal class Discriminator : DependentVeleroSecret.Discriminator<Gitea>()
}
