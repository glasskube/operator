package eu.glasskube.operator

import eu.glasskube.kubernetes.api.model.configMap
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.config.CloudProvider
import eu.glasskube.operator.config.ConfigGenerator
import eu.glasskube.operator.config.ConfigKey
import io.fabric8.kubernetes.api.model.ConfigMap
import io.fabric8.kubernetes.client.KubernetesClient
import io.fabric8.kubernetes.client.dsl.Resource
import io.quarkus.logging.Log
import io.quarkus.runtime.Startup
import java.security.SecureRandom
import java.util.Random
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces

@ApplicationScoped
class OperatorConfiguration(private val client: KubernetesClient) {
    @Startup
    fun initializeConfigIfNeed() {
        if (!config.exists) {
            Log.info("Initializing operator configuration.")
            client.resource(defaultConfigMap).create()
        } else {
            Log.info("Operator configuration already initialized.")
        }
    }

    private fun existsNodeWithLabel(label: String) = client.nodes().withLabel(label).list().items.isNotEmpty()
    private fun detectCloudProvider(): CloudProvider = when {
        existsNodeWithLabel("eks.amazonaws.com/nodegroup") -> CloudProvider.aws
        existsNodeWithLabel("csi.hetzner.cloud/location") -> CloudProvider.hcloud
        else -> CloudProvider.generic
    }

    private val config: Resource<ConfigMap>
        get() = client.configMaps().inNamespace(Environment.KUBERNETES_NAMESPACE).withName(ConfigGenerator.NAME)

    private val defaultConfigMap: ConfigMap
        get() = configMap {
            metadata {
                name = ConfigGenerator.NAME
                namespace = Environment.KUBERNETES_NAMESPACE
                labels = mapOf(ConfigGenerator.LABEL_SELECTOR to "")
            }
        }

    private fun getConfig(key: ConfigKey): String = config.get().data.getValue(key.name)

    private fun getConfigOrNull(key: ConfigKey): String? = config.get().data[key.name]

    val cloudProvider: CloudProvider
        get() = getConfigOrNull(ConfigKey.cloudProvider)?.let(CloudProvider::valueOf) ?: detectCloudProvider()

    val databaseStorageClassName: String
        get() = getConfig(ConfigKey.databaseStorageClassName)

    @Produces
    fun random(): Random = SecureRandom.getInstanceStrong()
}
