package eu.glasskube.operator.matomo.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.secret
import eu.glasskube.operator.matomo.Matomo
import eu.glasskube.operator.matomo.MatomoReconciler
import eu.glasskube.operator.matomo.resourceLabels
import eu.glasskube.operator.matomo.secretName
import io.fabric8.kubernetes.api.model.Secret
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = MatomoReconciler.SELECTOR)
class MatomoSecret : CRUDKubernetesDependentResource<Secret, Matomo>(Secret::class.java) {
    override fun desired(primary: Matomo, context: Context<Matomo>) = secret {
        metadata {
            name = primary.secretName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        stringData = mapOf(
            "MATOMO_DATABASE_PASSWORD" to ""
        )
    }
}
