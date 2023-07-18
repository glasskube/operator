package eu.glasskube.operator.apps.glitchtip.dependent

import eu.glasskube.operator.apps.glitchtip.Glitchtip
import eu.glasskube.operator.apps.glitchtip.GlitchtipReconciler
import eu.glasskube.operator.generic.condition.PostgresReadyCondition
import eu.glasskube.operator.generic.dependent.postgres.DependentPostgresCluster
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = GlitchtipReconciler.SELECTOR)
class GlitchtipPostgresCluster : DependentPostgresCluster<Glitchtip>(Glitchtip.Postgres) {
    class ReadyPostCondition : PostgresReadyCondition<Glitchtip>()

    override val Glitchtip.storageSize get() = "5Gi"
}
