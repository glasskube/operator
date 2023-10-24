package eu.glasskube.operator.apps.metabase.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.apps.metabase.Metabase
import eu.glasskube.operator.apps.metabase.MetabaseReconciler
import eu.glasskube.operator.apps.metabase.genericResourceName
import eu.glasskube.operator.apps.metabase.resourceLabels
import eu.glasskube.operator.generic.dependent.postgres.PostgresWithoutBackupsSpecCondition
import eu.glasskube.operator.infra.minio.MinioBucket
import eu.glasskube.operator.infra.minio.MinioBucketSpec
import eu.glasskube.operator.infra.minio.minioBucket
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = MetabaseReconciler.SELECTOR)
class MetabaseMinioBucket : CRUDKubernetesDependentResource<MinioBucket, Metabase>(MinioBucket::class.java) {
    internal class ReconcilePrecondition : PostgresWithoutBackupsSpecCondition<MinioBucket, Metabase>()

    override fun desired(primary: Metabase, context: Context<Metabase>) = minioBucket {
        metadata {
            name(primary.genericResourceName)
            namespace(primary.metadata.namespace)
            labels(primary.resourceLabels)
        }
        spec = MinioBucketSpec()
    }
}
