package eu.glasskube.operator.apps.gitea.dependent

import eu.glasskube.kubernetes.api.model.configMap
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.operator.api.reconciler.getSecondaryResource
import eu.glasskube.operator.apps.gitea.Gitea
import eu.glasskube.operator.apps.gitea.Gitea.Postgres.postgresHostName
import eu.glasskube.operator.apps.gitea.Gitea.Redis.redisName
import eu.glasskube.operator.apps.gitea.GiteaReconciler
import eu.glasskube.operator.apps.gitea.iniConfigMapName
import eu.glasskube.operator.apps.gitea.resourceLabels
import eu.glasskube.utils.decodeBase64
import eu.glasskube.utils.logger
import io.fabric8.kubernetes.api.model.ConfigMap
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(
    labelSelector = GiteaReconciler.SELECTOR,
    resourceDiscriminator = GiteaIniConfigMap.Discriminator::class
)
class GiteaIniConfigMap : CRUDKubernetesDependentResource<ConfigMap, Gitea>(ConfigMap::class.java) {
    internal class Discriminator : ResourceIDMatcherDiscriminator<ConfigMap, Gitea>({
        ResourceID(it.iniConfigMapName, it.namespace)
    })

    override fun desired(primary: Gitea, context: Context<Gitea>) = configMap {
        metadata {
            name(primary.iniConfigMapName)
            namespace(primary.namespace)
            labels(primary.resourceLabels)
        }
        data = primary.baseConfig + getSmtpConfig(primary, context)
    }

    private val Gitea.baseConfig: Map<String, String>
        get() = mapOf(
            "GITEA__database__DB_TYPE" to "postgres",
            "GITEA__database__HOST" to "$postgresHostName:5432",
            "GITEA__database__NAME" to "gitea",
            "GITEA__server__ROOT_URL" to "https://${spec.host}/",
            "GITEA__server__DOMAIN" to spec.host,
            "GITEA__server__SSH_DOMAIN" to spec.sshHost,
            "GITEA__server__DISABLE_SSH" to (!spec.sshEnabled).toString(),
            "GITEA__service__DISABLE_REGISTRATION" to (!spec.registrationEnabled).toString(),
            "GITEA__repository__ROOT" to "${GiteaDeployment.WORK_DIR}/data/gitea-repositories",
            "GITEA__security__INSTALL_LOCK" to "true",
            "GITEA__session__PROVIDER" to "db",
            "GITEA__indexer__ISSUE_INDEXER_TYPE" to "db",
            "GITEA__cache__ADAPTER" to "redis",
            "GITEA__cache__HOST" to "redis://$redisName:6379/0?pool_size=100&idle_timeout=180s",
            "GITEA__queue__TYPE" to "redis",
            "GITEA__queue__CONN_STR" to "redis://$redisName:6379/0?pool_size=100&idle_timeout=180s",
            "GITEA__metrics__ENABLED" to "true",
            "GITEA__webhook__ALLOWED_HOST_LIST" to "*"
        )

    private fun getSmtpConfig(primary: Gitea, context: Context<Gitea>): Map<String, String> =
        when (val smtp = primary.spec.smtp) {
            null -> emptyMap()
            else -> {
                val authSecret = context.client.secrets()
                    .inNamespace(primary.metadata.namespace)
                    .withName(smtp.authSecret.name)
                    .require()
                mapOf(
                    "GITEA__service__ENABLE_NOTIFY_MAIL" to "true",
                    "GITEA__mailer__ENABLED" to "true",
                    "GITEA__mailer__FROM" to smtp.fromAddress,
                    "GITEA__mailer__PROTOCOL" to if (smtp.tlsEnabled) "smtp+starttls" else "smtp",
                    "GITEA__mailer__SMTP_ADDR" to smtp.host,
                    "GITEA__mailer__SMTP_PORT" to smtp.port.toString(),
                    "GITEA__mailer__USER" to authSecret.data.getValue("username").decodeBase64(),
                    "GITEA__mailer__PASSWD" to authSecret.data.getValue("password").decodeBase64()
                )
            }
        }

    override fun onUpdated(primary: Gitea, updated: ConfigMap, actual: ConfigMap, context: Context<Gitea>) {
        super.onUpdated(primary, updated, actual, context)
        context.getSecondaryResource(GiteaDeployment.Discriminator()).ifPresent {
            log.info("Restarting deployment after config ini change")
            context.client.apps().deployments().resource(it).rolling().restart()
        }
    }

    companion object {
        private val log = logger()
    }
}
