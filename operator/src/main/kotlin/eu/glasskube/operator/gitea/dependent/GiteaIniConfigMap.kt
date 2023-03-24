package eu.glasskube.operator.gitea.dependent

import eu.glasskube.kubernetes.api.model.configMap
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.api.reconciler.getSecondaryResource
import eu.glasskube.operator.gitea.Gitea
import eu.glasskube.operator.gitea.GiteaReconciler
import eu.glasskube.operator.gitea.dbClusterName
import eu.glasskube.operator.gitea.iniConfigMapName
import eu.glasskube.operator.gitea.redisName
import eu.glasskube.operator.gitea.resourceLabels
import eu.glasskube.operator.logger
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
    internal class Discriminator : ResourceIDMatcherDiscriminator<ConfigMap, Gitea>({ ResourceID(it.iniConfigMapName) })

    override fun desired(primary: Gitea, context: Context<Gitea>) = configMap {
        metadata {
            name = primary.iniConfigMapName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        data = mapOf(
            "GITEA__database__DB_TYPE" to "postgres",
            "GITEA__database__HOST" to "${primary.dbClusterName}-rw:5432",
            "GITEA__database__NAME" to "gitea",
            "GITEA__server__ROOT_URL" to "https://${primary.spec.host}/",
            "GITEA__server__DOMAIN" to primary.spec.host,
            "GITEA__server__SSH_DOMAIN" to primary.spec.sshHost,
            "GITEA__service__DISABLE_REGISTRATION" to (!primary.spec.registrationEnabled).toString(),
            "GITEA__repository__ROOT" to "${Gitea.WORK_DIR}/data/gitea-repositories",
            "GITEA__security__INSTALL_LOCK" to "true",
            "GITEA__session__PROVIDER" to "db",
            "GITEA__indexer__ISSUE_INDEXER_TYPE" to "db",
            "GITEA__cache__ADAPTER" to "redis",
            "GITEA__cache__HOST" to "redis://${primary.redisName}:6379/0?pool_size=100&idle_timeout=180s",
            "GITEA__queue__TYPE" to "redis",
            "GITEA__queue__CONN_STR" to "redis://${primary.redisName}:6379/0?pool_size=100&idle_timeout=180s",
            "GITEA__metrics__ENABLED" to "true",
            "GITEA__webhook__ALLOWED_HOST_LIST" to "*"
        )
    }

    override fun onUpdated(primary: Gitea, updated: ConfigMap, actual: ConfigMap, context: Context<Gitea>) {
        super.onUpdated(primary, updated, actual, context)
        context.getSecondaryResource(GiteaDeployment.Discriminator()).ifPresent {
            log.info("Restarting deployment after config ini change")
            kubernetesClient.apps().deployments().resource(it).rolling().restart()
        }
    }

    companion object {
        private val log = logger()
    }
}
