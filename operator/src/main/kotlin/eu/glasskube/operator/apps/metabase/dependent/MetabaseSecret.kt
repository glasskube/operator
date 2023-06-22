package eu.glasskube.operator.apps.metabase.dependent

import eu.glasskube.operator.apps.metabase.Metabase
import eu.glasskube.operator.apps.metabase.MetabaseReconciler
import eu.glasskube.operator.apps.metabase.resourceLabels
import eu.glasskube.operator.apps.metabase.secretName
import eu.glasskube.operator.generic.dependent.GeneratedSecret
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = MetabaseReconciler.SELECTOR)
class MetabaseSecret : GeneratedSecret<Metabase>() {
    override val Metabase.generatedSecretName get() = secretName
    override val Metabase.generatedSecretLabels get() = resourceLabels
    override val generatedKeys get() = arrayOf("MB_ENCRYPTION_SECRET_KEY")
}
