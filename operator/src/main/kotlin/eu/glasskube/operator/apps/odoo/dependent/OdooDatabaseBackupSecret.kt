package eu.glasskube.operator.apps.odoo.dependent

import eu.glasskube.operator.apps.odoo.Odoo
import eu.glasskube.operator.apps.odoo.OdooReconciler
import eu.glasskube.operator.apps.odoo.dbBackupSecretName
import eu.glasskube.operator.apps.odoo.dbBackupUsername
import eu.glasskube.operator.apps.odoo.resourceLabels
import eu.glasskube.operator.generic.dependent.GeneratedSecret
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(
    labelSelector = OdooReconciler.SELECTOR
)
class OdooDatabaseBackupSecret : GeneratedSecret<Odoo>() {
    override val Odoo.generatedSecretName get() = dbBackupSecretName
    override val Odoo.generatedSecretLabels get() = resourceLabels
    override val generatedKeys get() = arrayOf("password")
    override val Odoo.generatedSecretData get() = mapOf("username" to dbBackupUsername)
    override val generatedSecretType = "kubernetes.io/basic-auth"
}
