package eu.glasskube.operator.apps.odoo.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.apps.odoo.Odoo
import eu.glasskube.operator.apps.odoo.OdooReconciler
import eu.glasskube.operator.apps.odoo.bucketName
import eu.glasskube.operator.apps.odoo.dbBackupSecretName
import eu.glasskube.operator.apps.odoo.genericResourceName
import eu.glasskube.operator.apps.odoo.resourceLabels
import eu.glasskube.operator.infra.minio.MinioBucket
import eu.glasskube.operator.infra.minio.MinioBucketSpec
import eu.glasskube.operator.infra.minio.minioBucket
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
