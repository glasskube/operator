package eu.glasskube.operator.apps.keycloak.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.operator.apps.keycloak.Keycloak
import eu.glasskube.operator.apps.keycloak.KeycloakReconciler
import eu.glasskube.operator.apps.keycloak.backupBucketName
import eu.glasskube.operator.apps.keycloak.resourceLabels
import eu.glasskube.operator.generic.dependent.postgres.PostgresWithoutBackupsSpecCondition
import eu.glasskube.operator.infra.minio.MinioBucket
import eu.glasskube.operator.infra.minio.MinioBucketSpec
import eu.glasskube.operator.infra.minio.minioBucket
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = KeycloakReconciler.SELECTOR)
class KeycloakPostgresBackupBucket : CRUDKubernetesDependentResource<MinioBucket, Keycloak>(MinioBucket::class.java) {
    internal class ReconcilePrecondition : PostgresWithoutBackupsSpecCondition<MinioBucket, Keycloak>()

    override fun desired(primary: Keycloak, context: Context<Keycloak>) = minioBucket {
        metadata {
            name(primary.backupBucketName)
            namespace(primary.namespace)
            labels(primary.resourceLabels)
        }
        spec = MinioBucketSpec()
    }
}
