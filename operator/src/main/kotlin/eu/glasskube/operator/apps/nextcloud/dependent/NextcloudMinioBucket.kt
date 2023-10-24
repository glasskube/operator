package eu.glasskube.operator.apps.nextcloud.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.operator.apps.nextcloud.Nextcloud
import eu.glasskube.operator.apps.nextcloud.NextcloudReconciler
import eu.glasskube.operator.apps.nextcloud.databaseBackupBucketName
import eu.glasskube.operator.apps.nextcloud.resourceLabels
import eu.glasskube.operator.generic.dependent.postgres.PostgresWithoutBackupsSpecCondition
import eu.glasskube.operator.infra.minio.MinioBucket
import eu.glasskube.operator.infra.minio.MinioBucketSpec
import eu.glasskube.operator.infra.minio.minioBucket
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = NextcloudReconciler.SELECTOR)
class NextcloudMinioBucket : CRUDKubernetesDependentResource<MinioBucket, Nextcloud>(MinioBucket::class.java) {
    internal class ReconcilePrecondition : PostgresWithoutBackupsSpecCondition<MinioBucket, Nextcloud>()

    override fun desired(primary: Nextcloud, context: Context<Nextcloud>) = minioBucket {
        metadata {
            name(primary.databaseBackupBucketName)
            namespace(primary.namespace)
            labels(primary.resourceLabels)
        }
        spec = MinioBucketSpec()
    }
}
