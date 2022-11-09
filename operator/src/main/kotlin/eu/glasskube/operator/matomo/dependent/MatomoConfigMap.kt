package eu.glasskube.operator.matomo.dependent

import eu.glasskube.kubernetes.api.model.configMap
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.matomo.Matomo
import eu.glasskube.operator.matomo.MatomoReconciler
import eu.glasskube.operator.matomo.configMapName
import eu.glasskube.operator.matomo.resourceLabels
import io.fabric8.kubernetes.api.model.ConfigMap
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = MatomoReconciler.SELECTOR)
class MatomoConfigMap : CRUDKubernetesDependentResource<ConfigMap, Matomo>(ConfigMap::class.java) {
    override fun desired(primary: Matomo, context: Context<Matomo>) = configMap {
        metadata {
            name = primary.configMapName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        data = mapOf(
            "MATOMO_DATABASE_HOST" to "matomo-${primary.metadata.name}-mariadb.${primary.metadata.namespace}.svc.cluster.local",
            "MATOMO_DATABASE_USERNAME" to "matomo",
            "MATOMO_DATABASE_DBNAME" to "matomo"
        )
    }
}
