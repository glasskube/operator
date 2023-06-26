package eu.glasskube.operator.boot

import io.fabric8.kubernetes.client.KubernetesClient
import io.javaoperatorsdk.operator.api.config.BaseConfigurationService
import io.javaoperatorsdk.operator.api.config.Utils
import org.springframework.stereotype.Component

@Component
class InjectionAwareConfigurationService(
    private val factory: InjectionAwareDependentResourceFactory,
    kubernetesClient: KubernetesClient
) : BaseConfigurationService(Utils.VERSION, null, kubernetesClient) {
    override fun dependentResourceFactory() = factory
}
