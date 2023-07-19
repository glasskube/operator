package eu.glasskube.operator.boot

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import eu.glasskube.operator.Environment
import eu.glasskube.operator.api.reconciler.HasRegistrationCondition
import eu.glasskube.utils.logger
import io.fabric8.kubernetes.api.model.HasMetadata
import io.fabric8.kubernetes.client.KubernetesClient
import io.fabric8.kubernetes.client.KubernetesClientBuilder
import io.fabric8.kubernetes.client.utils.KubernetesSerialization
import io.javaoperatorsdk.operator.Operator
import io.javaoperatorsdk.operator.RegisteredController
import io.javaoperatorsdk.operator.api.config.ConfigurationService
import io.javaoperatorsdk.operator.api.config.ControllerConfigurationOverrider
import io.javaoperatorsdk.operator.api.reconciler.Constants
import io.javaoperatorsdk.operator.api.reconciler.Reconciler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.security.SecureRandom
import java.util.Random

@Configuration
class OperatorConfig {
    @Bean
    fun objectMapper(): ObjectMapper =
        jsonMapper {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            addModule(kotlinModule { enable(KotlinFeature.NullIsSameAsDefault) })
        }

    @Bean(destroyMethod = "close")
    fun kubernetesClient(objectMapper: ObjectMapper): KubernetesClient =
        KubernetesClientBuilder()
            .withKubernetesSerialization(KubernetesSerialization(objectMapper, true))
            .build()

    @Bean(destroyMethod = "stop")
    fun operator(configurationService: ConfigurationService, reconcilers: List<Reconciler<*>>) =
        Operator(configurationService).apply {
            reconcilers.forEach {
                if (it !is HasRegistrationCondition || it.isRegistrationEnabled) {
                    registerForNamespaceOrCluster(it)
                } else {
                    log.warn(
                        listOfNotNull(
                            "Reconciler was not registered because it's registration condition is not met: ${it.javaClass.name}.",
                            it.registrationConditionHint,
                            "Resources managed by this controller will not be reconciled!"
                        ).joinToString(" ")
                    )
                }
            }
            start()
        }

    @Bean
    fun random(): Random =
        SecureRandom.getInstanceStrong()

    private fun <T : HasMetadata> Operator.registerForNamespaceOrCluster(reconciler: Reconciler<T>): RegisteredController<T> =
        register(reconciler) { it.settingNamespaceFromEnv() }

    private fun <T : HasMetadata> ControllerConfigurationOverrider<T>.settingNamespaceFromEnv(): ControllerConfigurationOverrider<T> =
        when (Environment.MANAGE_CURRENT_NAMESPACE) {
            "true", "yes", "1" -> settingNamespace(Constants.WATCH_CURRENT_NAMESPACE)
            else -> when (val namespace = Environment.MANAGE_NAMESPACE) {
                null -> this
                else -> settingNamespace(namespace)
            }
        }

    companion object {
        @JvmStatic
        private val log = logger()
    }
}
