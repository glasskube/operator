package eu.glasskube.operator.generic.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.secret
import eu.glasskube.operator.secrets.SecretGenerator
import io.fabric8.kubernetes.api.model.HasMetadata
import io.fabric8.kubernetes.api.model.Secret
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.ResourceUpdatePreProcessor

abstract class GeneratedSecret<T : HasMetadata> :
    CRUDKubernetesDependentResource<Secret, T>(Secret::class.java),
    ResourceUpdatePreProcessor<Secret> {

    abstract val T.generatedSecretName: String
    abstract val T.generatedSecretLabels: Map<String, String>
    abstract val generatedKeys: Array<String>
    open val T.generatedSecretNamespace: String get() = metadata.namespace
    open val T.generatedSecretData: Map<String, String>? get() = null
    open val generatedSecretType: String? get() = null

    override fun desired(primary: T, context: Context<T>) = secret {
        metadata {
            name = primary.generatedSecretName
            namespace = primary.generatedSecretNamespace
            labels = primary.generatedSecretLabels + SecretGenerator.LABEL
            annotations = mapOf(SecretGenerator.generateKeys(*generatedKeys))
        }
        generatedSecretType?.let { type = it }
        primary.generatedSecretData?.let { stringData = it }
    }

    override fun replaceSpecOnActual(actual: Secret, desired: Secret, context: Context<*>) = actual.apply {
        metadata.annotations.putAll(desired.metadata.annotations)
        metadata.labels.putAll(desired.metadata.labels)
        data.putAll(desired.data)
        stringData.putAll(desired.stringData)
    }
}
