package eu.glasskube.operator.config

import eu.glasskube.operator.getCloudProvider
import io.fabric8.kubernetes.api.model.ConfigMap
import io.fabric8.kubernetes.client.KubernetesClient
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration
import io.javaoperatorsdk.operator.api.reconciler.Reconciler
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger(ConfigGenerator::class.java)

@ControllerConfiguration(
    labelSelector = ConfigGenerator.LABEL_SELECTOR,
    generationAwareEventProcessing = false
)
class ConfigGenerator(private val kubernetesClient: KubernetesClient) : Reconciler<ConfigMap> {

    init {
        log.info("config initializing")
    }

    override fun reconcile(resource: ConfigMap, context: Context<ConfigMap>): UpdateControl<ConfigMap> {
        log.info("Reconciling ${resource.kind} ${resource.metadata.name}@${resource.metadata.namespace}")

        enumValues<ConfigKey>().forEach {
            if (resource.data[it.name].isNullOrBlank()) {
                resource.data[it.name] = createConfigFor(it)
            }
        }

        return UpdateControl.updateResource(resource)
    }

    private fun createConfigFor(it: ConfigKey): String {
        return when (it) {
            ConfigKey.cloudProvider -> getCloudProvider(this.kubernetesClient).name
            ConfigKey.databaseStorageClassName -> detectDatabaseStorageClass()
        }
    }

    private fun detectDatabaseStorageClass(): String {
        return when (getCloudProvider(kubernetesClient)) {
            CloudProvider.aws -> "gp3-encrypted"
            CloudProvider.minikube -> "standard"
        }
    }

    companion object {
        const val NAME = "glasskube-config"
        const val LABEL_SELECTOR = "glasskube.eu/config"
    }
}

enum class ConfigKey {
    cloudProvider,
    databaseStorageClassName
}

enum class CloudProvider {
    aws,
    minikube
}
