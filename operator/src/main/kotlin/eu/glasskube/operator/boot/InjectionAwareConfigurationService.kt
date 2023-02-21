package eu.glasskube.operator.boot

import com.fasterxml.jackson.databind.ObjectMapper
import io.javaoperatorsdk.operator.api.config.BaseConfigurationService
import org.springframework.stereotype.Component

@Component
class InjectionAwareConfigurationService(
    private val factory: InjectionAwareDependentResourceFactory,
    private val mapper: ObjectMapper
) : BaseConfigurationService() {
    override fun dependentResourceFactory() = factory
    override fun getObjectMapper() = mapper
}
