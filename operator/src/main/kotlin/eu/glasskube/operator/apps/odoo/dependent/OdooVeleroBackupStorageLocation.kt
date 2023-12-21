package eu.glasskube.operator.apps.odoo.dependent

import eu.glasskube.operator.apps.odoo.Odoo
import eu.glasskube.operator.apps.odoo.OdooReconciler
import eu.glasskube.operator.generic.dependent.backups.DependentVeleroBackupStorageLocation
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = OdooReconciler.SELECTOR)
class OdooVeleroBackupStorageLocation : DependentVeleroBackupStorageLocation<Odoo>()
