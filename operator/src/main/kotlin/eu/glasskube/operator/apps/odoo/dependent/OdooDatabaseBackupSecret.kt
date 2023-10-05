package eu.glasskube.operator.apps.odoo.dependent

import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.operator.apps.odoo.Odoo
import eu.glasskube.operator.apps.odoo.OdooReconciler
import eu.glasskube.operator.apps.odoo.dbBackupSecretName
import eu.glasskube.operator.apps.odoo.dbBackupUsername
import eu.glasskube.operator.apps.odoo.resourceLabels
import eu.glasskube.operator.generic.dependent.GeneratedSecret
import eu.glasskube.operator.generic.dependent.postgres.PostgresWithoutBackupsSpecCondition
import io.fabric8.kubernetes.api.model.Secret
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(
    labelSelector = OdooReconciler.SELECTOR,
    resourceDiscriminator = OdooDatabaseBackupSecret.Discriminator::class
)
class OdooDatabaseBackupSecret : GeneratedSecret<Odoo>() {
    internal class ReconcilePrecondition : PostgresWithoutBackupsSpecCondition<Secret, Odoo>()
    internal class Discriminator :
        ResourceIDMatcherDiscriminator<Secret, Odoo>({ ResourceID(it.dbBackupSecretName, it.namespace) })

    override val Odoo.generatedSecretName get() = dbBackupSecretName
    override val Odoo.generatedSecretLabels get() = resourceLabels
    override val generatedKeys get() = arrayOf("password")
    override val Odoo.generatedSecretData get() = mapOf("username" to dbBackupUsername)
    override val generatedSecretType = "kubernetes.io/basic-auth"
}
