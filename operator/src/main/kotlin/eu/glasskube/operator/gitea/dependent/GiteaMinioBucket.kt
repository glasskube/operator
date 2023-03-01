package eu.glasskube.operator.gitea.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.gitea.Gitea
import eu.glasskube.operator.gitea.GiteaReconciler
import eu.glasskube.operator.gitea.genericResourceName
import eu.glasskube.operator.gitea.resourceLabels
import eu.glasskube.operator.minio.MinioBucket
import eu.glasskube.operator.minio.MinioBucketSpec
import eu.glasskube.operator.minio.minioBucket
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = GiteaReconciler.SELECTOR)
class GiteaMinioBucket : CRUDKubernetesDependentResource<MinioBucket, Gitea>(MinioBucket::class.java) {
    override fun desired(primary: Gitea, context: Context<Gitea>) = minioBucket {
        metadata {
            name = primary.genericResourceName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        spec = MinioBucketSpec()
    }
}
