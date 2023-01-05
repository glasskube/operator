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
        log.info("Glasskube settings are initializing")
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
        val storageClasses = kubernetesClient.storage().v1().storageClasses().list()
        val storageClassNames = storageClasses.items.map { it.metadata.name }
        val defaultStorageClass = storageClasses.items.find { p -> p.metadata.annotations.get("storageclass.kubernetes.io/is-default-class") == "true" }
        val defaultStorageClassName = defaultStorageClass?.let { defaultStorageClass.metadata.name } ?: "standard"

        val awsEncryptedStorageClass = "gp3-encrypted"
        if (getCloudProvider(kubernetesClient) === CloudProvider.aws && storageClassNames.contains(awsEncryptedStorageClass)) {
            return awsEncryptedStorageClass
        }

        return defaultStorageClassName
    }

    companion object {
        const val NAME = "glasskube-settings"
        const val LABEL_SELECTOR = "glasskube.eu/settings"
    }
}

enum class ConfigKey {
    cloudProvider,
    databaseStorageClassName
}

enum class CloudProvider {
    aws,
    hcloud,
    generic,

    @Deprecated("please use `generic` instead")
    minikube
}
