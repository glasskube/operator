package eu.glasskube.operator.config

import io.fabric8.kubernetes.api.model.ConfigMap
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration
import io.javaoperatorsdk.operator.api.reconciler.Reconciler
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl
import org.apache.commons.lang3.RandomStringUtils
import org.slf4j.LoggerFactory
import java.util.Random

private val log = LoggerFactory.getLogger(Config::class.java)

@ControllerConfiguration(
    labelSelector = Config.LABEL_SELECTOR,
    generationAwareEventProcessing = false
)
class Config : Reconciler<ConfigMap> {

    init {
        log.info("config initializing")
    }

    override fun reconcile(resource: ConfigMap, context: Context<ConfigMap>): UpdateControl<ConfigMap> {
        log.info("Reconciling ${resource.kind} ${resource.metadata.name}@${resource.metadata.namespace}")

        val keys = resource.metadata.annotations[CONFIG_KEYS]?.split(',')?.toSet().orEmpty()

        log.info("ConfigMap keys: $keys")

        if (!keys.isEmpty()) {
            log.info("No update required")
            return UpdateControl.noUpdate()
        }

        log.info("Update required")

//        resource.metadata.annotations[CONFIG_KEYS] = keys.joinToString(",")

        return UpdateControl.updateResource(resource)
    }

    private fun Random.nextString(count: Int) =
        RandomStringUtils.random(count, 0, 0, true, true, null, this)

    companion object {
        const val NAME = "glasskube-config"
        const val LABEL_SELECTOR = "glasskube.eu/config"
        const val CONFIG_KEYS = "config.glasskube.eu/configKeys"
        fun generateKeys(vararg keys: String) = CONFIG_KEYS to keys.joinToString(",")
    }
}
