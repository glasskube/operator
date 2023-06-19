package eu.glasskube.operator.apps.matomo.dependent

import eu.glasskube.kubernetes.api.model.configMap
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.apps.matomo.Matomo
import eu.glasskube.operator.apps.matomo.MatomoReconciler
import eu.glasskube.operator.apps.matomo.configMapName
import eu.glasskube.operator.apps.matomo.databaseName
import eu.glasskube.operator.apps.matomo.databaseUser
import eu.glasskube.operator.apps.matomo.mariaDBHost
import eu.glasskube.operator.apps.matomo.resourceLabels
import eu.glasskube.operator.resourceAsString
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
            "MATOMO_DATABASE_HOST" to host(primary),
            "MATOMO_DATABASE_USERNAME" to primary.databaseUser,
            "MATOMO_DATABASE_DBNAME" to primary.databaseName,
            "init.sh" to initScript,
            "install.json" to primary.installJson,
            "glasskube-matomo-archive-cron" to primary.cron
        )
    }

    private val initScript
        get() = resourceAsString("init.sh")
    private val Matomo.installJson
        get() = replaceDatabaseValues(this@MatomoConfigMap.resourceAsString("config.json"), this)
    private val Matomo.cron
        get() = replaceHost(this@MatomoConfigMap.resourceAsString("cron"), this)

    private fun replaceHost(cron: String, primary: Matomo) = cron.replace("%HOST%", primary.spec.host!!)

    private fun replaceDatabaseValues(config: String, primary: Matomo) =
        config.replace("%MATOMO_DATABASE_HOST%", host(primary))
            .replace("%MATOMO_DATABASE_USERNAME%", primary.databaseUser)
            .replace("%MATOMO_DATABASE_DBNAME%", primary.databaseName)

    private fun host(primary: Matomo) = "${primary.mariaDBHost}.${primary.metadata.namespace}"
}
