package eu.glasskube.operator.apps.metabase.dependent

import eu.glasskube.operator.apps.metabase.Metabase
import eu.glasskube.operator.generic.dependent.backups.BackupSpecNotNullCondition
import eu.glasskube.operator.generic.dependent.backups.DependentVeleroSecret
import io.fabric8.kubernetes.api.model.Secret
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(resourceDiscriminator = MetabaseVeleroSecret.Discriminator::class)
class MetabaseVeleroSecret : DependentVeleroSecret<Metabase>() {
    internal class ReconcilePrecondition : BackupSpecNotNullCondition<Secret, Metabase>()
    internal class Discriminator : DependentVeleroSecret.Discriminator<Metabase>()
}
