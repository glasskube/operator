package eu.glasskube.operator.apps.odoo.dependent

import eu.glasskube.operator.apps.odoo.Odoo
import eu.glasskube.operator.generic.dependent.backups.BackupSpecNotNullCondition
import eu.glasskube.operator.generic.dependent.backups.DependentVeleroSecret
import io.fabric8.kubernetes.api.model.Secret
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(resourceDiscriminator = OdooVeleroSecret.Discriminator::class)
class OdooVeleroSecret : DependentVeleroSecret<Odoo>() {
    internal class ReconcilePrecondition : BackupSpecNotNullCondition<Secret, Odoo>()
    internal class Discriminator : DependentVeleroSecret.Discriminator<Odoo>()
}
