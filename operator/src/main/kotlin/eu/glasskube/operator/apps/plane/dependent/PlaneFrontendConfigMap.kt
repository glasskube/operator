package eu.glasskube.operator.apps.plane.dependent

import eu.glasskube.kubernetes.api.model.configMap
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.operator.api.reconciler.getSecondaryResource
import eu.glasskube.operator.apps.plane.Plane
import eu.glasskube.operator.apps.plane.frontendResourceLabels
import eu.glasskube.operator.apps.plane.frontendResourceName
import eu.glasskube.utils.logger
import io.fabric8.kubernetes.api.model.ConfigMap
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(resourceDiscriminator = PlaneFrontendConfigMap.Discriminator::class)
class PlaneFrontendConfigMap : CRUDKubernetesDependentResource<ConfigMap, Plane>(ConfigMap::class.java) {
    internal class Discriminator :
        ResourceIDMatcherDiscriminator<ConfigMap, Plane>({ ResourceID(it.frontendResourceName, it.namespace) })

    override fun desired(primary: Plane, context: Context<Plane>) = configMap {
        metadata {
            name(primary.frontendResourceName)
            namespace(primary.namespace)
            labels(primary.frontendResourceLabels)
        }
        data = mapOf(
            "NEXT_PUBLIC_ENABLE_OAUTH" to "0",
            "NEXT_PUBLIC_ENABLE_SENTRY" to "0",
            "NEXT_PUBLIC_ENABLE_SESSION_RECORDER" to "0",
            "NEXT_PUBLIC_TRACK_EVENTS" to "0",
            "NEXT_PUBLIC_SLACK_CLIENT_ID" to "",
            "NEXT_PUBLIC_PLAUSIBLE_DOMAIN" to "",
            "NEXT_PUBLIC_DEPLOY_URL" to "https://${primary.spec.host}/spaces",
            "NEXT_PUBLIC_API_BASE_URL" to "https://${primary.spec.host}"
        )
    }

    override fun onUpdated(primary: Plane, updated: ConfigMap, actual: ConfigMap, context: Context<Plane>) {
        super.onUpdated(primary, updated, actual, context)
        context.getSecondaryResource(PlaneFrontendDeployment.Discriminator()).ifPresent {
            log.info("Restarting frontend Deployment after ConfigMap changed")
            context.client.apps().deployments().resource(it).rolling().restart()
        }
    }

    companion object {
        private val log = logger()
    }
}
