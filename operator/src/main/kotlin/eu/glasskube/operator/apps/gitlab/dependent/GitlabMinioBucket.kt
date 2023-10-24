package eu.glasskube.operator.apps.gitlab.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.apps.gitlab.Gitlab
import eu.glasskube.operator.apps.gitlab.GitlabReconciler
import eu.glasskube.operator.apps.gitlab.genericResourceName
import eu.glasskube.operator.apps.gitlab.resourceLabels
import eu.glasskube.operator.generic.dependent.postgres.PostgresWithoutBackupsSpecCondition
import eu.glasskube.operator.infra.minio.MinioBucket
import eu.glasskube.operator.infra.minio.MinioBucketSpec
import eu.glasskube.operator.infra.minio.minioBucket
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = GitlabReconciler.SELECTOR)
class GitlabMinioBucket : CRUDKubernetesDependentResource<MinioBucket, Gitlab>(MinioBucket::class.java) {
    internal class ReconcilePrecondition : PostgresWithoutBackupsSpecCondition<MinioBucket, Gitlab>()

    override fun desired(primary: Gitlab, context: Context<Gitlab>) = minioBucket {
        metadata {
            name(primary.genericResourceName)
            namespace(primary.metadata.namespace)
            labels(primary.resourceLabels)
        }
        spec = MinioBucketSpec()
    }
}
