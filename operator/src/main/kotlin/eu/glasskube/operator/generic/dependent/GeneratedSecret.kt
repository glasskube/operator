package eu.glasskube.operator.generic.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.secret
import eu.glasskube.operator.encodeBase64
import eu.glasskube.operator.infra.secrets.SecretGenerator
import io.fabric8.kubernetes.api.model.HasMetadata
import io.fabric8.kubernetes.api.model.Secret
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import kotlin.jvm.optionals.getOrNull

abstract class GeneratedSecret<T : HasMetadata> : CRUDKubernetesDependentResource<Secret, T>(Secret::class.java) {
    abstract val T.generatedSecretName: String
    abstract val T.generatedSecretLabels: Map<String, String>
    abstract val generatedKeys: Array<String>
    open val T.generatedSecretNamespace: String get() = metadata.namespace
    open val T.generatedSecretData: Map<String, String>? get() = null
    open val generatedSecretType = "Opaque"

    override fun desired(primary: T, context: Context<T>) = secret {
        val existing = getSecondaryResource(primary, context)
        val existingAnnotations = existing
            .map { it.metadata.annotations.filterKeys { key -> key == SecretGenerator.GENERATED_KEYS } }
            .getOrNull()
            .orEmpty()
        val existingData = existing.map { it.data }.getOrNull().orEmpty()

        metadata {
            name = primary.generatedSecretName
            namespace = primary.generatedSecretNamespace
            labels = primary.generatedSecretLabels + SecretGenerator.LABEL
            annotations = existingAnnotations + mapOf(SecretGenerator.generateKeys(*generatedKeys))
        }
        type = generatedSecretType
        data = existingData + primary.generatedSecretData?.mapValues { (_, it) -> it.encodeBase64() }.orEmpty()
    }
}
