package eu.glasskube.operator.config

import eu.glasskube.operator.OperatorConfiguration
import io.fabric8.kubernetes.api.model.ConfigMap
import io.fabric8.kubernetes.client.KubernetesClient
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration
import io.javaoperatorsdk.operator.api.reconciler.Reconciler
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl
import io.quarkus.logging.Log

@ControllerConfiguration(
    labelSelector = ConfigGenerator.LABEL_SELECTOR,
    generationAwareEventProcessing = false
)
class ConfigGenerator(
    private val kubernetesClient: KubernetesClient,
    private val config: OperatorConfiguration
) : Reconciler<ConfigMap> {

    override fun reconcile(resource: ConfigMap, context: Context<ConfigMap>): UpdateControl<ConfigMap> {
        Log.info("Reconciling ${resource.kind} ${resource.metadata.name}@${resource.metadata.namespace}")

        enumValues<ConfigKey>().forEach {
            if (resource.data[it.name].isNullOrBlank()) {
                resource.data[it.name] = createConfigFor(it)
            }
        }

        return UpdateControl.updateResource(resource)
    }

    private fun createConfigFor(it: ConfigKey): String {
        return when (it) {
            ConfigKey.cloudProvider -> config.cloudProvider.name
            ConfigKey.databaseStorageClassName -> detectDatabaseStorageClass()
        }
    }

    private fun detectDatabaseStorageClass(): String {
        val storageClasses = kubernetesClient.storage().v1().storageClasses().list()
        val storageClassNames = storageClasses.items.map { it.metadata.name }
        val defaultStorageClass =
            storageClasses.items.find { p -> p.metadata.annotations["storageclass.kubernetes.io/is-default-class"].toBoolean() }

        val awsEncryptedStorageClass = "gp3-encrypted"

        return if (config.cloudProvider === CloudProvider.aws && storageClassNames.contains(awsEncryptedStorageClass)) {
            awsEncryptedStorageClass
        } else {
            defaultStorageClass?.metadata?.name ?: "standard"
        }
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
