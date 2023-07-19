package eu.glasskube.operator.apps.metabase.dependent

import eu.glasskube.kubernetes.api.model.configMap
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.api.reconciler.getSecondaryResource
import eu.glasskube.operator.apps.metabase.Metabase
import eu.glasskube.operator.apps.metabase.Metabase.Postgres.postgresHostName
import eu.glasskube.operator.apps.metabase.MetabaseReconciler
import eu.glasskube.operator.apps.metabase.MetabaseSmtp
import eu.glasskube.operator.apps.metabase.configMapName
import eu.glasskube.operator.apps.metabase.genericResourceName
import eu.glasskube.operator.apps.metabase.resourceLabels
import eu.glasskube.utils.logger
import io.fabric8.kubernetes.api.model.ConfigMap
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = MetabaseReconciler.SELECTOR)
class MetabaseConfigMap : CRUDKubernetesDependentResource<ConfigMap, Metabase>(ConfigMap::class.java) {

    override fun desired(primary: Metabase, context: Context<Metabase>) = configMap {
        metadata {
            name = primary.configMapName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        data = primary.run { baseData + smtpData }
    }

    override fun onUpdated(primary: Metabase, updated: ConfigMap, actual: ConfigMap, context: Context<Metabase>) {
        super.onUpdated(primary, updated, actual, context)
        context.getSecondaryResource<Deployment>().ifPresent {
            log.info("Restarting deployment after config change")
            kubernetesClient.apps().deployments().resource(it).rolling().restart()
        }
    }

    private val Metabase.baseData: Map<String, String>
        get() = mapOf(
            "MB_DB_TYPE" to "postgres",
            "MB_DB_HOST" to postgresHostName,
            "MB_DB_DBNAME" to "metabase",
            "MB_DB_PORT" to "5432",
            "MB_SITE_NAME" to "Metabase $genericResourceName",
            "MB_SITE_URL" to "http://${spec.host}"
        )

    private val Metabase.smtpData: Map<String, String>
        get() = spec.smtp
            ?.run {
                mapOf(
                    "MB_EMAIL_SMTP_HOST" to host,
                    "MB_EMAIL_SMTP_PORT" to port.toString(),
                    "MB_EMAIL_SMTP_SECURITY" to smtpSecurity,
                    "MB_EMAIL_FROM_ADDRESS" to fromAddress
                )
            }
            ?: emptyMap()

    private val MetabaseSmtp.smtpSecurity: String
        get() = if (tlsEnabled) {
            "tls"
        } else {
            "none"
        }

    companion object {
        @JvmStatic
        private val log = logger()
    }
}
