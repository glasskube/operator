package eu.glasskube.operator.apps.plane.dependent

import eu.glasskube.kubernetes.api.model.configMap
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.operator.api.reconciler.getSecondaryResource
import eu.glasskube.operator.apps.plane.Plane
import eu.glasskube.operator.apps.plane.spaceResourceLabels
import eu.glasskube.operator.apps.plane.spaceResourceName
import eu.glasskube.utils.logger
import io.fabric8.kubernetes.api.model.ConfigMap
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(resourceDiscriminator = PlaneSpaceConfigMap.Discriminator::class)
class PlaneSpaceConfigMap : CRUDKubernetesDependentResource<ConfigMap, Plane>(ConfigMap::class.java) {
    internal class Discriminator :
        ResourceIDMatcherDiscriminator<ConfigMap, Plane>({ ResourceID(it.spaceResourceName) })

    override fun desired(primary: Plane, context: Context<Plane>) = configMap {
        metadata {
            name = primary.spaceResourceName
            namespace = primary.namespace
            labels = primary.spaceResourceLabels
        }
        data = mapOf(
            "NEXT_PUBLIC_GOOGLE_CLIENTID" to "",
            "NEXT_PUBLIC_ENABLE_OAUTH" to "0",
            "NEXT_PUBLIC_API_BASE_URL" to "https://${primary.spec.host}"
        )
    }

    override fun onUpdated(primary: Plane, updated: ConfigMap, actual: ConfigMap, context: Context<Plane>) {
        super.onUpdated(primary, updated, actual, context)
        context.getSecondaryResource(PlaneSpaceDeployment.Discriminator()).ifPresent {
            log.info("Restarting space Deployment after ConfigMap changed")
            kubernetesClient.apps().deployments().resource(it).rolling().restart()
        }
    }

    companion object {
        private val log = logger()
    }
}
