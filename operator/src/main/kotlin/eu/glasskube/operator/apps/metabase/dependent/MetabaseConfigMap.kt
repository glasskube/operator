package eu.glasskube.operator.apps.metabase.dependent

import eu.glasskube.kubernetes.api.model.configMap
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.apps.metabase.Metabase
import eu.glasskube.operator.apps.metabase.MetabaseReconciler
import eu.glasskube.operator.apps.metabase.configMapName
import eu.glasskube.operator.apps.metabase.dbClusterName
import eu.glasskube.operator.apps.metabase.genericResourceName
import eu.glasskube.operator.apps.metabase.resourceLabels
import io.fabric8.kubernetes.api.model.ConfigMap
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(labelSelector = MetabaseReconciler.SELECTOR)
class MetabaseConfigMap : CRUDKubernetesDependentResource<ConfigMap, Metabase>(ConfigMap::class.java) {
    internal class Discriminator : ResourceIDMatcherDiscriminator<ConfigMap, Metabase>({ ResourceID(it.configMapName) })

    override fun desired(primary: Metabase, context: Context<Metabase>) = configMap {
        metadata {
            name = primary.configMapName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        data = mapOf(
            "MB_DB_TYPE" to "postgres",
            "MB_DB_HOST" to "${primary.dbClusterName}-rw",
            "MB_DB_DBNAME" to "metabase",
            "MB_DB_PORT" to "5432",
            "MB_SITE_NAME" to "Metabase ${primary.genericResourceName}",
            "MB_SITE_URL" to "http://${primary.spec.host}"
        )
    }
}
