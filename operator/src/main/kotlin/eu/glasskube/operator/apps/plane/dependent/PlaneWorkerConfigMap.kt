package eu.glasskube.operator.apps.plane.dependent

import eu.glasskube.kubernetes.api.model.configMap
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.operator.apps.plane.Plane
import eu.glasskube.operator.apps.plane.workerResourceLabels
import eu.glasskube.operator.apps.plane.workerResourceName
import eu.glasskube.utils.resourceProperty
import io.fabric8.kubernetes.api.model.ConfigMap
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(resourceDiscriminator = PlaneWorkerConfigMap.Discriminator::class)
class PlaneWorkerConfigMap : CRUDKubernetesDependentResource<ConfigMap, Plane>(ConfigMap::class.java) {

    internal class Discriminator :
        ResourceIDMatcherDiscriminator<ConfigMap, Plane>({ ResourceID(it.workerResourceName, it.namespace) })

    private val workerEntrypointSh by resourceProperty()

    override fun desired(primary: Plane, context: Context<Plane>) = configMap {
        metadata {
            name(primary.workerResourceName)
            namespace(primary.namespace)
            labels(primary.workerResourceLabels)
        }
        data = mapOf(
            PlaneWorkerDeployment.ENTRYPOINT_NAME to workerEntrypointSh
        )
    }
}
