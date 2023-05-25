package eu.glasskube.operator.config

import eu.glasskube.operator.logger
import io.fabric8.kubernetes.api.model.ConfigMap
import io.fabric8.kubernetes.client.KubernetesClient
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration
import io.javaoperatorsdk.operator.api.reconciler.Reconciler
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl

@ControllerConfiguration(
    labelSelector = ConfigGenerator.LABEL_SELECTOR,
    generationAwareEventProcessing = false
)
class ConfigGenerator(private val kubernetesClient: KubernetesClient, private val configService: ConfigService) :
    Reconciler<ConfigMap> {

    init {
        log.info("Glasskube settings are initializing")
    }

    override fun reconcile(resource: ConfigMap, context: Context<ConfigMap>): UpdateControl<ConfigMap> {
        log.info("Reconciling ${resource.kind} ${resource.metadata.name}@${resource.metadata.namespace}")

        enumValues<ConfigKey>().forEach {
            if (resource.data[it.name].isNullOrBlank()) {
                createConfigFor(it)?.let { value -> resource.data[it.name] = value }
            }
        }

        return UpdateControl.updateResource(resource)
    }

    private fun createConfigFor(it: ConfigKey): String? {
        return when (it) {
            ConfigKey.cloudProvider -> configService.cloudProvider.name
            ConfigKey.databaseStorageClassName -> detectDatabaseStorageClass()
            ConfigKey.commonIngressAnnotations,
            ConfigKey.commonLoadBalancerAnnotations,
            ConfigKey.ingressClassName -> null
        }
    }

    private fun detectDatabaseStorageClass(): String {
        val storageClasses = kubernetesClient.storage().v1().storageClasses().list()
        val storageClassNames = storageClasses.items.map { it.metadata.name }
        val defaultStorageClass =
            storageClasses.items.find { p -> p.metadata.annotations["storageclass.kubernetes.io/is-default-class"] == "true" }
        val defaultStorageClassName = defaultStorageClass?.let { defaultStorageClass.metadata.name } ?: "standard"

        val awsEncryptedStorageClass = "gp3-encrypted"
        if (configService.cloudProvider === CloudProvider.aws && storageClassNames.contains(awsEncryptedStorageClass)) {
            return awsEncryptedStorageClass
        }

        return defaultStorageClassName
    }

    companion object {
        private val log = logger()
        const val NAME = "glasskube-settings"
        const val LABEL_SELECTOR = "glasskube.eu/settings"
    }
}

enum class ConfigKey {
    cloudProvider,
    commonIngressAnnotations,
    commonLoadBalancerAnnotations,
    databaseStorageClassName,
    ingressClassName
}

enum class CloudProvider {
    aws,
    hcloud,
    generic,

    @Deprecated("please use `generic` instead")
    minikube
}
