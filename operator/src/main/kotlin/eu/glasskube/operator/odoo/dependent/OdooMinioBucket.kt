package eu.glasskube.operator.odoo.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.minio.MinioBucket
import eu.glasskube.operator.minio.MinioBucketSpec
import eu.glasskube.operator.minio.minioBucket
import eu.glasskube.operator.odoo.Odoo
import eu.glasskube.operator.odoo.OdooReconciler
import eu.glasskube.operator.odoo.bucketName
import eu.glasskube.operator.odoo.dbBackupSecretName
import eu.glasskube.operator.odoo.genericResourceName
import eu.glasskube.operator.odoo.resourceLabels
import io.fabric8.kubernetes.api.model.LocalObjectReference
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = OdooReconciler.SELECTOR)
class OdooMinioBucket : CRUDKubernetesDependentResource<MinioBucket, Odoo>(MinioBucket::class.java) {
    override fun desired(primary: Odoo, context: Context<Odoo>) = minioBucket {
        metadata {
            name = primary.genericResourceName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        spec = MinioBucketSpec(
            userSecret = LocalObjectReference(primary.dbBackupSecretName),
            bucketNameOverride = primary.bucketName
        )
    }
}
