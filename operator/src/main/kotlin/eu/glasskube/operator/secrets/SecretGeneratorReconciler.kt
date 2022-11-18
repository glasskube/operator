package eu.glasskube.operator.secrets

import io.fabric8.kubernetes.api.model.Secret
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration
import io.javaoperatorsdk.operator.api.reconciler.Reconciler
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl
import org.slf4j.LoggerFactory
import java.util.*

private val log = LoggerFactory.getLogger(SecretGeneratorReconciler::class.java)

@ControllerConfiguration(
    labelSelector = SecretGeneratorReconciler.LABEL,
    generationAwareEventProcessing = false
)
class SecretGeneratorReconciler(private val random: Random) : Reconciler<Secret> {
    override fun reconcile(resource: Secret, context: Context<Secret>): UpdateControl<Secret> {
        log.info("Reconciling ${resource.kind} ${resource.metadata.name}@${resource.metadata.namespace}")
        val generateKeys = resource.metadata.annotations[GENERATE_KEYS]?.split(',')?.toSet().orEmpty()
        val generatedKeys = resource.metadata.annotations[GENERATED_KEYS]?.split(',')?.toSet().orEmpty()

        log.debug("Desired keys: $generateKeys")
        log.debug("Existing keys: $generatedKeys")

        if (generateKeys == generatedKeys) {
            log.info("No update required")
            return UpdateControl.noUpdate()
        }

        log.info("Update required")

        (generateKeys - generatedKeys).forEach { resource.stringData[it] = random.nextInt().toString() }
        (generatedKeys - generateKeys).forEach { resource.data.remove(it) }

        resource.metadata.annotations[GENERATED_KEYS] = generateKeys.joinToString(separator = ",")

        return UpdateControl.updateResource(resource)
    }

    companion object {
        const val LABEL = "glasskube.eu/generated=yes"
        const val GENERATE_KEYS = "secrets.glasskube.eu/generateKeys"
        const val GENERATED_KEYS = "secrets.glasskube.eu/generatedKeys"
    }
}
