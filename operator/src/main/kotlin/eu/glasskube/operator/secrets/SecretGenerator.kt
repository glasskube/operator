package eu.glasskube.operator.secrets

import io.fabric8.kubernetes.api.model.Secret
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration
import io.javaoperatorsdk.operator.api.reconciler.Reconciler
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl
import io.quarkus.logging.Log
import org.apache.commons.lang3.RandomStringUtils
import java.util.Random

@ControllerConfiguration(
    labelSelector = SecretGenerator.LABEL_SELECTOR,
    generationAwareEventProcessing = false
)
class SecretGenerator(private val random: Random) : Reconciler<Secret> {
    override fun reconcile(resource: Secret, context: Context<Secret>): UpdateControl<Secret> {
        Log.info("Reconciling ${resource.kind} ${resource.metadata.name}@${resource.metadata.namespace}")
        val generateKeys = resource.metadata.annotations[GENERATE_KEYS]?.split(',')?.toSet().orEmpty()
        val generatedKeys = resource.metadata.annotations[GENERATED_KEYS]?.split(',')?.toSet().orEmpty()

        Log.info("Desired keys: $generateKeys")
        Log.info("Existing keys: $generatedKeys")

        if (generateKeys == generatedKeys) {
            Log.info("No update required")
            return UpdateControl.noUpdate()
        }

        Log.info("Update required")

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
