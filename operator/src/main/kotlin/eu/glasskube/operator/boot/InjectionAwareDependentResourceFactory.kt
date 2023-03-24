package eu.glasskube.operator.boot

import eu.glasskube.operator.logger
import io.javaoperatorsdk.operator.api.config.ControllerConfiguration
import io.javaoperatorsdk.operator.api.config.dependent.DependentResourceConfigurationResolver
import io.javaoperatorsdk.operator.api.config.dependent.DependentResourceSpec
import io.javaoperatorsdk.operator.api.reconciler.dependent.DependentResource
import io.javaoperatorsdk.operator.api.reconciler.dependent.DependentResourceFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

@Component
class InjectionAwareDependentResourceFactory :
    DependentResourceFactory<ControllerConfiguration<*>>,
    ApplicationContextAware {

    private lateinit var applicationContext: ApplicationContext

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

    override fun createFrom(
        spec: DependentResourceSpec<*, *>,
        configuration: ControllerConfiguration<*>
    ): DependentResource<*, *> {
        log.info("Creating dependent resource {}", spec.name)
        return applicationContext.autowireCapableBeanFactory.createBean(spec.dependentResourceClass)
            .also { DependentResourceConfigurationResolver.configure(it, spec, configuration) }
    }

    companion object {
        private val log = logger()
    }
}
