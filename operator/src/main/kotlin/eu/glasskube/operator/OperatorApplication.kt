package eu.glasskube.operator

import eu.glasskube.operator.boot.VeleroProperties
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType

@EnableConfigurationProperties(
    VeleroProperties::class
)
@ComponentScan(
    includeFilters = [
        ComponentScan.Filter(type = FilterType.ANNOTATION, value = [ControllerConfiguration::class])
    ]
)
@SpringBootApplication
class OperatorApplication(releaseInfo: ReleaseInfo) {
    init {
        releaseInfo.print()
    }
}

fun main(args: Array<String>) {
    runApplication<OperatorApplication>(*args)
}
