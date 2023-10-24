package eu.glasskube.operator.apps.glitchtip.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.apps.glitchtip.Glitchtip
import eu.glasskube.operator.apps.glitchtip.GlitchtipReconciler
import eu.glasskube.operator.apps.glitchtip.genericResourceName
import eu.glasskube.operator.apps.glitchtip.resourceLabels
import eu.glasskube.operator.generic.dependent.postgres.PostgresWithoutBackupsSpecCondition
import eu.glasskube.operator.infra.minio.MinioBucket
import eu.glasskube.operator.infra.minio.MinioBucketSpec
import eu.glasskube.operator.infra.minio.minioBucket
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = GlitchtipReconciler.SELECTOR)
class GlitchtipMinioBucket : CRUDKubernetesDependentResource<MinioBucket, Glitchtip>(MinioBucket::class.java) {
    internal class ReconcilePrecondition : PostgresWithoutBackupsSpecCondition<MinioBucket, Glitchtip>()

    override fun desired(primary: Glitchtip, context: Context<Glitchtip>) = minioBucket {
        metadata {
            name(primary.genericResourceName)
            namespace(primary.metadata.namespace)
            labels(primary.resourceLabels)
        }
        spec = MinioBucketSpec()
    }
}
