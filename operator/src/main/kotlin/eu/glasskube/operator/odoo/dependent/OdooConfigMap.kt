package eu.glasskube.operator.odoo.dependent

import eu.glasskube.kubernetes.api.model.configMap
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.odoo.Odoo
import eu.glasskube.operator.odoo.OdooReconciler
import eu.glasskube.operator.odoo.configMapName
import eu.glasskube.operator.odoo.resourceLabels
import io.fabric8.kubernetes.api.model.ConfigMap
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = OdooReconciler.SELECTOR)
class OdooConfigMap : CRUDKubernetesDependentResource<ConfigMap, Odoo>(ConfigMap::class.java) {
    override fun desired(primary: Odoo, context: Context<Odoo>) = configMap {
        metadata {
            name = primary.configMapName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        data = mapOf(
            Odoo.configFile to """
                [options]
                addons_path = /mnt/extra-addons
                data_dir = ${Odoo.volumePath}
                proxy_mode = True
                gevent_port = 8072
                list_db = False
            """.trimIndent()
        )
    }
}
