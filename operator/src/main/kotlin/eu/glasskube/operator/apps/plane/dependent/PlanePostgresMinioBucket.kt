package eu.glasskube.operator.apps.plane.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.operator.apps.plane.Plane
import eu.glasskube.operator.apps.plane.Plane.Postgres.postgresClusterLabels
import eu.glasskube.operator.apps.plane.Plane.Postgres.postgresClusterName
import eu.glasskube.operator.generic.dependent.postgres.backup.PostgresMinioBucketPrecondition
import eu.glasskube.operator.infra.minio.MinioBucket
import eu.glasskube.operator.infra.minio.MinioBucketSpec
import eu.glasskube.operator.infra.minio.minioBucket
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent
class PlanePostgresMinioBucket : CRUDKubernetesDependentResource<MinioBucket, Plane>(MinioBucket::class.java) {
    internal class ReconcilePrecondition : PostgresMinioBucketPrecondition<MinioBucket, Plane>()

    override fun desired(primary: Plane, context: Context<Plane>) = minioBucket {
        metadata {
            name = primary.postgresClusterName
            namespace = primary.namespace
            labels = primary.postgresClusterLabels
        }
        spec = MinioBucketSpec()
    }
}
