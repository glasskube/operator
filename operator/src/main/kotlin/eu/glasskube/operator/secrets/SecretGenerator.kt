package eu.glasskube.operator.secrets

import io.fabric8.kubernetes.api.model.Secret
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration
import io.javaoperatorsdk.operator.api.reconciler.Reconciler
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl
import java.util.*
import org.apache.commons.lang3.RandomStringUtils
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger(SecretGenerator::class.java)

@ControllerConfiguration(
    labelSelector = SecretGenerator.LABEL_SELECTOR,
    generationAwareEventProcessing = false
)
class SecretGenerator(private val random: Random) : Reconciler<Secret> {
    override fun reconcile(resource: Secret, context: Context<Secret>): UpdateControl<Secret> {
        log.info("Reconciling ${resource.kind} ${resource.metadata.name}@${resource.metadata.namespace}")
        val generateKeys = resource.metadata.annotations[GENERATE_KEYS]?.split(',')?.toSet().orEmpty()
        val generatedKeys = resource.metadata.annotations[GENERATED_KEYS]?.split(',')?.toSet().orEmpty()

        log.info("Desired keys: $generateKeys")
        log.info("Existing keys: $generatedKeys")

        if (generateKeys == generatedKeys) {
            log.info("No update required")
            return UpdateControl.noUpdate()
        }

        log.info("Update required")

        (generateKeys - generatedKeys).forEach { resource.stringData[it] = random.nextString(32) }
        (generatedKeys - generateKeys).forEach { resource.data.remove(it) }

        resource.metadata.annotations[GENERATED_KEYS] = generateKeys.joinToString(",")

        return UpdateControl.updateResource(resource)
    }

    private fun Random.nextString(count: Int) =
        RandomStringUtils.random(count, 0, 0, true, true, null, this)

    companion object {
        val LABEL = "glasskube.eu/generated" to "yes"
        const val LABEL_SELECTOR = "glasskube.eu/generated=yes"
        const val GENERATE_KEYS = "secrets.glasskube.eu/generateKeys"
        const val GENERATED_KEYS = "secrets.glasskube.eu/generatedKeys"
        fun generateKeys(vararg keys: String) = GENERATE_KEYS to keys.joinToString(",")
    }
}
