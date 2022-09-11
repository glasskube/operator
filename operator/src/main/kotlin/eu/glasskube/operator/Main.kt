package eu.glasskube.operator

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import eu.glasskube.operator.controller.WebPageReconciler
import io.fabric8.kubernetes.client.KubernetesClientBuilder
import io.javaoperatorsdk.operator.Operator
import org.slf4j.LoggerFactory

private val LOG = LoggerFactory.getLogger("main")

fun main() {
    LOG.info("Glasskube Operator is starting")

    val client = KubernetesClientBuilder().build()
    val operator = Operator(client) {
        it.withObjectMapper(jacksonObjectMapper())
    }
    operator.register(WebPageReconciler())
    operator.start()
}
