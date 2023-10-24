package eu.glasskube.operator.apps.gitlab.dependent

import eu.glasskube.kubernetes.api.model.configMap
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.api.reconciler.getSecondaryResource
import eu.glasskube.operator.apps.gitlab.Gitlab
import eu.glasskube.operator.apps.gitlab.Gitlab.Postgres.postgresHostName
import eu.glasskube.operator.apps.gitlab.GitlabReconciler
import eu.glasskube.operator.apps.gitlab.configMapName
import eu.glasskube.operator.apps.gitlab.resourceLabels
import eu.glasskube.utils.logger
import eu.glasskube.utils.resourceAsString
import io.fabric8.kubernetes.api.model.ConfigMap
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = GitlabReconciler.SELECTOR)
class GitlabConfigMap : CRUDKubernetesDependentResource<ConfigMap, Gitlab>(ConfigMap::class.java) {

    private val gitlabOmnibusConfig: String
        get() = resourceAsString("gitlab.rb")

    override fun desired(primary: Gitlab, context: Context<Gitlab>) = configMap {
        metadata {
            name(primary.configMapName)
            namespace(primary.metadata.namespace)
            labels(primary.resourceLabels)
        }
        data = primary.run { baseData + sshData + smtpData }
    }

    override fun onUpdated(primary: Gitlab, updated: ConfigMap, actual: ConfigMap, context: Context<Gitlab>) {
        super.onUpdated(primary, updated, actual, context)
        context.getSecondaryResource<Deployment>().ifPresent {
            log.info("Restarting deployment after config change")
            context.client.apps().deployments().resource(it).rolling().restart()
        }
    }

    private val Gitlab.baseData: Map<String, String>
        get() = mapOf(
            "GITLAB_HOST" to spec.host,
            "DB_HOST" to postgresHostName,
            "GITLAB_OMNIBUS_CONFIG" to (spec.omnibusConfigOverride ?: gitlabOmnibusConfig)
        )

    private val Gitlab.sshData: Map<String, String>
        get() = spec.sshHost?.let { mapOf("GITLAB_SSH_HOST" to it) }.orEmpty()

    private val Gitlab.smtpData: Map<String, String>
        get() = spec.smtp
            ?.run {
                mapOf(
                    "SMTP_ENABLED" to true.toString(),
                    "SMTP_HOST" to host,
                    "SMTP_PORT" to port.toString(),
                    "SMTP_TLS_ENABLED" to tlsEnabled.toString(),
                    "SMTP_FROM_ADDRESS" to fromAddress
                )
            }
            ?: mapOf("SMTP_ENABLED" to false.toString())

    companion object {
        @JvmStatic
        private val log = logger()
    }
}
