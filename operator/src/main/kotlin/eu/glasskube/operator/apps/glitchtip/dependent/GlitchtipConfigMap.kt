package eu.glasskube.operator.apps.glitchtip.dependent

import eu.glasskube.kubernetes.api.model.configMap
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.api.reconciler.getSecondaryResource
import eu.glasskube.operator.apps.glitchtip.Glitchtip
import eu.glasskube.operator.apps.glitchtip.Glitchtip.Postgres.postgresHostName
import eu.glasskube.operator.apps.glitchtip.Glitchtip.Redis.redisName
import eu.glasskube.operator.apps.glitchtip.GlitchtipReconciler
import eu.glasskube.operator.apps.glitchtip.configMapName
import eu.glasskube.operator.apps.glitchtip.resourceLabels
import eu.glasskube.operator.logger
import io.fabric8.kubernetes.api.model.ConfigMap
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = GlitchtipReconciler.SELECTOR)
class GlitchtipConfigMap : CRUDKubernetesDependentResource<ConfigMap, Glitchtip>(ConfigMap::class.java) {

    override fun desired(primary: Glitchtip, context: Context<Glitchtip>) = configMap {
        metadata {
            name = primary.configMapName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        data = primary.run { baseData }
    }

    override fun onUpdated(primary: Glitchtip, updated: ConfigMap, actual: ConfigMap, context: Context<Glitchtip>) {
        super.onUpdated(primary, updated, actual, context)
        context.getSecondaryResource<Deployment>().ifPresent {
            log.info("Restarting deployment after config change")
            kubernetesClient.apps().deployments().resource(it).rolling().restart()
        }
    }

    private val Glitchtip.baseData: Map<String, String>
        get() = mapOf(
            "CELERY_WORKER_AUTOSCALE" to "1,3",
            "CELERY_WORKER_MAX_TASKS_PER_CHILD" to "10000",
            "GLITCHTIP_DOMAIN" to "https://${spec.host}",
            "I_PAID_FOR_GLITCHTIP" to "true",
            "ENABLE_SOCIAL_AUTH" to "false",
            "ENABLE_USER_REGISTRATION" to spec.registrationEnabled.toString(),
            "ENABLE_ORGANIZATION_CREATION" to spec.organizationCreationEnabled.toString(),
            "DATABASE_HOST" to postgresHostName,
            "DATABASE_NAME" to "glitchtip",
            "DATABASE_PORT" to "5432",
            "REDIS_URL" to "redis://$redisName:6379/0"
        )

    companion object {
        @JvmStatic
        private val log = logger()
    }
}
