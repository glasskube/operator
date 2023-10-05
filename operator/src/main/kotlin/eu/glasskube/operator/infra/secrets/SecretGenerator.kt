package eu.glasskube.operator.infra.secrets

import eu.glasskube.kubernetes.api.model.loggingId
import eu.glasskube.utils.logger
import io.fabric8.kubernetes.api.model.Secret
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration
import io.javaoperatorsdk.operator.api.reconciler.Reconciler
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl
import org.apache.commons.lang3.RandomStringUtils
import java.util.Random

@ControllerConfiguration(
    labelSelector = SecretGenerator.LABEL_SELECTOR,
    generationAwareEventProcessing = false
)
class SecretGenerator(private val random: Random) : Reconciler<Secret> {
    override fun reconcile(resource: Secret, context: Context<Secret>): UpdateControl<Secret> {
        val generateKeys = resource.metadata.annotations[GENERATE_KEYS]?.split(',')?.toSet().orEmpty()
        val generatedKeys = resource.metadata.annotations[GENERATED_KEYS]?.split(',')?.toSet().orEmpty()

        log.debug("{} desired keys: {}", resource.loggingId, generateKeys)
        log.debug("{} existing keys: {}", resource.loggingId, generatedKeys)

        if (generateKeys == generatedKeys) {
            log.debug("{} no update required", resource.loggingId)
            return UpdateControl.noUpdate()
        }

        log.info("{} update required", resource.loggingId)

        (generateKeys - generatedKeys).forEach { resource.stringData[it] = random.nextString(32) }
        (generatedKeys - generateKeys).forEach { resource.data.remove(it) }

        resource.metadata.annotations[GENERATED_KEYS] = generateKeys.joinToString(",")

        return UpdateControl.updateResource(resource)
    }

    private fun Random.nextString(count: Int) =
        RandomStringUtils.random(count, 0, 0, true, true, null, this)

    companion object {
        private val log = logger()
        val LABEL = "glasskube.eu/generated" to "yes"
        const val LABEL_SELECTOR = "glasskube.eu/generated=yes"
        const val GENERATE_KEYS = "secrets.glasskube.eu/generateKeys"
        const val GENERATED_KEYS = "secrets.glasskube.eu/generatedKeys"
        fun generateKeys(vararg keys: String) = GENERATE_KEYS to keys.joinToString(",")
    }
}
