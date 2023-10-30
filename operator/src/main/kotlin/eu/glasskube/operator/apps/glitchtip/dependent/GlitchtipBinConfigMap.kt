package eu.glasskube.operator.apps.glitchtip.dependent

import eu.glasskube.kubernetes.api.model.configMap
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.operator.apps.glitchtip.Glitchtip
import eu.glasskube.operator.apps.glitchtip.GlitchtipReconciler
import eu.glasskube.operator.apps.glitchtip.binResourceName
import eu.glasskube.operator.apps.glitchtip.resourceLabels
import eu.glasskube.utils.encodeBase64
import eu.glasskube.utils.resourceAsString
import io.fabric8.kubernetes.api.model.ConfigMap
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(
    labelSelector = GlitchtipReconciler.SELECTOR,
    resourceDiscriminator = GlitchtipBinConfigMap.Discriminator::class
)
class GlitchtipBinConfigMap : CRUDKubernetesDependentResource<ConfigMap, Glitchtip>(ConfigMap::class.java) {
    internal class Discriminator : ResourceIDMatcherDiscriminator<ConfigMap, Glitchtip>({
        ResourceID(it.binResourceName, it.namespace)
    })

    override fun desired(primary: Glitchtip, context: Context<Glitchtip>) = configMap {
        metadata {
            name(primary.binResourceName)
            namespace(primary.metadata.namespace)
            labels(primary.resourceLabels)
        }
        binaryData = mapOf(GlitchtipDeployment.RUN_SH to runShResource.encodeBase64())
    }

    private val runShResource get() = resourceAsString(GlitchtipDeployment.RUN_SH)
}
