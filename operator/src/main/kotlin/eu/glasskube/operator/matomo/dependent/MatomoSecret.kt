package eu.glasskube.operator.matomo.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.secret
import eu.glasskube.operator.matomo.Matomo
import eu.glasskube.operator.matomo.MatomoReconciler
import eu.glasskube.operator.matomo.resourceLabels
import eu.glasskube.operator.matomo.secretName
import eu.glasskube.operator.secrets.SecretGenerator
import io.fabric8.kubernetes.api.model.Secret
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.ResourceUpdatePreProcessor

@KubernetesDependent(labelSelector = MatomoReconciler.SELECTOR)
class MatomoSecret :
    CRUDKubernetesDependentResource<Secret, Matomo>(Secret::class.java),
    ResourceUpdatePreProcessor<Secret> {
    override fun desired(primary: Matomo, context: Context<Matomo>) = secret {
        metadata {
            name = primary.secretName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels + SecretGenerator.LABEL
            annotations = mapOf(SecretGenerator.generateKeys("MATOMO_DATABASE_PASSWORD"))
        }
    }

    override fun replaceSpecOnActual(actual: Secret, desired: Secret, context: Context<*>) = actual
}
