package eu.glasskube.operator.gitea.dependent

import eu.glasskube.kubernetes.api.model.configMap
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.gitea.Gitea
import eu.glasskube.operator.gitea.GiteaReconciler
import eu.glasskube.operator.gitea.configMapName
import eu.glasskube.operator.gitea.resourceLabels
import io.fabric8.kubernetes.api.model.ConfigMap
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(
    labelSelector = GiteaReconciler.SELECTOR,
    resourceDiscriminator = GiteaConfigMap.Discriminator::class
)
class GiteaConfigMap : CRUDKubernetesDependentResource<ConfigMap, Gitea>(ConfigMap::class.java) {
    internal class Discriminator : ResourceIDMatcherDiscriminator<ConfigMap, Gitea>({ ResourceID(it.configMapName) })

    override fun desired(primary: Gitea, context: Context<Gitea>) = configMap {
        metadata {
            name = primary.configMapName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        data = mapOf(
            "GITEA_WORK_DIR" to Gitea.WORK_DIR
        )
    }
}
