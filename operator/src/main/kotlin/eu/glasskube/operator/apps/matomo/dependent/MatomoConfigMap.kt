package eu.glasskube.operator.apps.matomo.dependent

import eu.glasskube.kubernetes.api.model.configMap
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.api.reconciler.getSecondaryResource
import eu.glasskube.operator.apps.matomo.Matomo
import eu.glasskube.operator.apps.matomo.MatomoReconciler
import eu.glasskube.operator.apps.matomo.configMapName
import eu.glasskube.operator.apps.matomo.databaseName
import eu.glasskube.operator.apps.matomo.databaseUser
import eu.glasskube.operator.apps.matomo.mariaDBHost
import eu.glasskube.operator.apps.matomo.resourceLabels
import eu.glasskube.operator.logger
import eu.glasskube.operator.resourceAsString
import io.fabric8.kubernetes.api.model.ConfigMap
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.fabric8.kubernetes.client.dsl.internal.apps.v1.RollingUpdater
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
            "MATOMO_DATABASE_HOST" to primary.databaseHost,
            "MATOMO_DATABASE_USERNAME" to primary.databaseUser,
            "MATOMO_DATABASE_DBNAME" to primary.databaseName,
            "MATOMO_DATABASE_TABLES_PREFIX" to "matomo_",
            "MATOMO_FIRST_USER_NAME" to "root",
            "MATOMO_FIRST_USER_PASSWORD" to "glasskube-operator",
            "MATOMO_FIRST_SITE_NAME" to "Example Site",
            "MATOMO_FIRST_SITE_URL" to "www.example.com",
            "MATOMO_INSTALL_FILE" to MatomoDeployment.installJsonPath,
            MatomoDeployment.initSh to initSh,
            MatomoDeployment.installSh to installSh,
            MatomoDeployment.installJson to installJson,
            MatomoDeployment.archiveCron to primary.archiveCron
        )
    }

    override fun onUpdated(primary: Matomo, updated: ConfigMap, actual: ConfigMap, context: Context<Matomo>) {
        super.onUpdated(primary, updated, actual, context)
        context.getSecondaryResource<Deployment>().ifPresent {
            log.info("restarting deployment after configmap changed")
            RollingUpdater.restart(kubernetesClient.resource(it))
        }
    }

    private val initSh get() = resourceAsString("init.sh")
    private val installSh get() = resourceAsString("install.sh")
    private val installJson get() = this.resourceAsString("config.json")
    private val Matomo.archiveCron
        get() = replaceHost(this@MatomoConfigMap.resourceAsString("cron"), this)

    private fun replaceHost(cron: String, primary: Matomo) = cron.replace("%HOST%", primary.spec.host!!)

    private val Matomo.databaseHost get() = "$mariaDBHost.${metadata.namespace}"

    companion object {
        @JvmStatic
        private val log = logger()
    }
}
