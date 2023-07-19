package eu.glasskube.operator.apps.vault.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.operator.apps.vault.Vault
import eu.glasskube.operator.apps.vault.VaultReconciler
import eu.glasskube.operator.apps.vault.databaseBackupBucketName
import eu.glasskube.operator.apps.vault.resourceLabels
import eu.glasskube.operator.infra.minio.MinioBucket
import eu.glasskube.operator.infra.minio.MinioBucketSpec
import eu.glasskube.operator.infra.minio.minioBucket
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = VaultReconciler.SELECTOR)
class VaultMinioBucket : CRUDKubernetesDependentResource<MinioBucket, Vault>(MinioBucket::class.java) {
    override fun desired(primary: Vault, context: Context<Vault>) = minioBucket {
        metadata {
            name = primary.databaseBackupBucketName
            namespace = primary.namespace
            labels = primary.resourceLabels
        }
        spec = MinioBucketSpec()
    }
}
