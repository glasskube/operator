package eu.glasskube.operator

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import eu.glasskube.operator.httpecho.HttpEchoReconciler
import eu.glasskube.operator.matomo.MatomoReconciler
import eu.glasskube.operator.secrets.SecretGeneratorReconciler
import eu.glasskube.operator.webpage.WebPageReconciler
import io.fabric8.kubernetes.client.KubernetesClientBuilder
import io.javaoperatorsdk.operator.Operator
import org.slf4j.LoggerFactory
import java.security.SecureRandom
import java.time.Duration

private val LOG = LoggerFactory.getLogger("main")

fun main() {
    val startTime = System.nanoTime()
    println(
        """
   ____ _               _          _
  / ___| | __ _ ___ ___| | ___   _| |__   ___
 | |  _| |/ _` / __/ __| |/ / | | | '_ \ / _ \
 | |_| | | (_| \__ \__ \   <| |_| | |_) |  __/
  \____|_|\__,_|___/___/_|\_\\__,_|_.__/ \___|
        """
    )

    val client = KubernetesClientBuilder().build()
    val random = SecureRandom.getInstanceStrong()
    val operator = Operator(client) {
        it.withObjectMapper(jacksonObjectMapper())
    }
    operator.register(WebPageReconciler())
    operator.register(HttpEchoReconciler(client))
    operator.register(MatomoReconciler())
    operator.register(SecretGeneratorReconciler(random))
    operator.installShutdownHook()
    operator.start()
    LOG.info("\uD83E\uDDCA Glasskube started in {} seconds", Duration.ofNanos(System.nanoTime() - startTime).seconds)
}
