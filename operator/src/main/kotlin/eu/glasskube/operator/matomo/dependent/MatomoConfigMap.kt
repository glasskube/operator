package eu.glasskube.operator.matomo.dependent

import eu.glasskube.kubernetes.api.model.configMap
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.matomo.Matomo
import eu.glasskube.operator.matomo.MatomoReconciler
import eu.glasskube.operator.matomo.configMapName
import eu.glasskube.operator.matomo.databaseName
import eu.glasskube.operator.matomo.databaseUser
import eu.glasskube.operator.matomo.mariaDBHost
import eu.glasskube.operator.matomo.resourceLabels
import io.fabric8.kubernetes.api.model.ConfigMap
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import java.nio.charset.Charset

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
            "init.sh" to readFile("init.sh"),
            "install.json" to replaceDatabaseValues(readFile("config.json"), primary)
        )
    }

    private fun replaceDatabaseValues(config: String, primary: Matomo): String {
        return config.replace("%MATOMO_DATABASE_HOST%", host(primary))
            .replace("%MATOMO_DATABASE_USERNAME%", primary.databaseUser)
            .replace("%MATOMO_DATABASE_DBNAME%", primary.databaseName)
    }

    private fun host(primary: Matomo): String {
        return "${primary.mariaDBHost}.${primary.metadata.namespace}.svc.cluster.local"
    }

    private fun readFile(fileName: String): String {
        return this::class.java.getResource(fileName)?.readText(Charset.defaultCharset()) ?: "EMPTY"
    }
}
