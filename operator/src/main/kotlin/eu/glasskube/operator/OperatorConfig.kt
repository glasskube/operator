package eu.glasskube.operator

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.kotlinModule
import eu.glasskube.kubernetes.api.model.configMap
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.config.ConfigGenerator
import io.fabric8.kubernetes.api.model.HasMetadata
import io.fabric8.kubernetes.client.KubernetesClient
import io.fabric8.kubernetes.client.KubernetesClientBuilder
import io.fabric8.kubernetes.client.utils.Serialization
import io.javaoperatorsdk.operator.Operator
import io.javaoperatorsdk.operator.RegisteredController
import io.javaoperatorsdk.operator.api.config.ControllerConfigurationOverrider
import io.javaoperatorsdk.operator.api.reconciler.Constants
import io.javaoperatorsdk.operator.api.reconciler.Reconciler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.security.SecureRandom
import java.util.Random
import java.util.function.Consumer

@Configuration
class OperatorConfig {

    @Bean
    fun objectMapper(): ObjectMapper =
        Serialization.jsonMapper()
            .registerModule(kotlinModule { enable(KotlinFeature.NullIsSameAsDefault) })

    @Bean(destroyMethod = "close")
    fun kubernetesClient(): KubernetesClient =
        KubernetesClientBuilder().build().also {
            initializeConfigIfNeed(it)
        }

    @Bean(destroyMethod = "stop")
    fun operator(kubernetesClient: KubernetesClient, objectMapper: ObjectMapper, reconcilers: List<Reconciler<*>>) =
        Operator(kubernetesClient) { it.withObjectMapper(objectMapper) }
            .apply {
                reconcilers.forEach { registerForNamespaceOrCluster(it) }
                start()
            }

    @Bean
    fun random(): Random =
        SecureRandom.getInstanceStrong()

    private fun initializeConfigIfNeed(client: KubernetesClient) {
        val configMap = getConfig(client)
        if (!configMap.isReady) {
            client.resource(
                configMap {
                    metadata {
                        name = ConfigGenerator.NAME
                        namespace = Environment.NAMESPACE
                        labels = mapOf(ConfigGenerator.LABEL_SELECTOR to "")
                    }
                }
            ).create()
        }
    }

    private fun <T : HasMetadata> Operator.registerForNamespaceOrCluster(reconciler: Reconciler<T>): RegisteredController<T> =
        register(reconciler, Consumer { it.settingNamespaceFromEnv() })

    private fun <T : HasMetadata> ControllerConfigurationOverrider<T>.settingNamespaceFromEnv(): ControllerConfigurationOverrider<T> =
        when (Environment.MANAGE_CURRENT_NAMESPACE) {
            "true", "yes", "1" -> settingNamespace(Constants.WATCH_CURRENT_NAMESPACE)
            else -> when (val namespace = Environment.MANAGE_NAMESPACE) {
                null -> this
                else -> settingNamespace(namespace)
            }
        }

}
