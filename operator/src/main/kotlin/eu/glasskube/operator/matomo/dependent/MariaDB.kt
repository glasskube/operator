package eu.glasskube.operator.matomo.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.matomo.Matomo
import eu.glasskube.operator.matomo.MatomoReconciler
import eu.glasskube.operator.matomo.configMapName
import eu.glasskube.operator.matomo.resourceLabels
import io.fabric8.kubernetes.client.CustomResource
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = MatomoReconciler.SELECTOR)
class MariaDB : CRUDKubernetesDependentResource<CustomResource, Matomo>(CustomResource::class.java) {

    override fun desired(primary: Matomo, context: Context<Matomo>) = CustomResource {
        metadata {
            name = primary.configMapName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        data = mapOf(
            "MATOMO_DATABASE_HOST" to "mariadb.${primary.metadata.namespace}.svc.cluster.local",
            "MATOMO_DATABASE_USERNAME" to "user",
            "MATOMO_DATABASE_DBNAME" to "mariadb"
        )
    }
}
