package eu.glasskube.operator

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import eu.glasskube.kubernetes.api.model.configMap
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.config.Config
import eu.glasskube.operator.httpecho.HttpEchoReconciler
import eu.glasskube.operator.matomo.MatomoReconciler
import eu.glasskube.operator.secrets.SecretGenerator
import io.fabric8.kubernetes.api.model.HasMetadata
import io.fabric8.kubernetes.client.KubernetesClient
import io.fabric8.kubernetes.client.KubernetesClientBuilder
import io.javaoperatorsdk.operator.Operator
import io.javaoperatorsdk.operator.RegisteredController
import io.javaoperatorsdk.operator.api.config.ControllerConfigurationOverrider
import io.javaoperatorsdk.operator.api.reconciler.Constants
import io.javaoperatorsdk.operator.api.reconciler.Reconciler
import java.security.SecureRandom
import java.time.Duration
import java.util.function.Consumer
import org.slf4j.LoggerFactory

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

    initializeConfigIfNeed(client);


    val random = SecureRandom.getInstanceStrong()
    val operator = Operator(client) {
        it.withObjectMapper(jacksonObjectMapper())
    }

    operator.registerForNamespaceOrCluster(Config())
//    operator.registerForNamespaceOrCluster(WebPageReconciler())
    operator.registerForNamespaceOrCluster(HttpEchoReconciler(client))
    operator.registerForNamespaceOrCluster(MatomoReconciler())
    operator.registerForNamespaceOrCluster(SecretGenerator(random))
    operator.installShutdownHook()
    operator.start()
    LOG.info("\uD83E\uDDCA Glasskube started in {} seconds", Duration.ofNanos(System.nanoTime() - startTime).seconds)
}

fun initializeConfigIfNeed(client: KubernetesClient) {
    val configMap = client.configMaps().inNamespace(Environment.NAMESPACE).withName(Config.NAME)
    if (!configMap.isReady) {
        client.resource(configMap {
            metadata {
                name = Config.NAME
                namespace = Environment.NAMESPACE
                labels = mapOf(Config.LABEL_SELECTOR to "")
            }
        }).create()
    }
}

fun <T : HasMetadata> Operator.registerForNamespaceOrCluster(reconciler: Reconciler<T>): RegisteredController<T> =
    register(reconciler, Consumer { it.settingNamespaceFromEnv() })

fun <T : HasMetadata> ControllerConfigurationOverrider<T>.settingNamespaceFromEnv(): ControllerConfigurationOverrider<T> =
    when (Environment.MANAGE_CURRENT_NAMESPACE) {
        "true", "yes", "1" -> settingNamespace(Constants.WATCH_CURRENT_NAMESPACE)
        else -> when (val namespace = Environment.MANAGE_NAMESPACE) {
            null -> this
            else -> settingNamespace(namespace)
        }
    }
