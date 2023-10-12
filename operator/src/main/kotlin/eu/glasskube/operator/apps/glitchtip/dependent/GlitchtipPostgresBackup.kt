package eu.glasskube.operator.apps.glitchtip.dependent

import eu.glasskube.operator.apps.glitchtip.Glitchtip
import eu.glasskube.operator.apps.glitchtip.GlitchtipReconciler
import eu.glasskube.operator.generic.dependent.postgres.DependentPostgresScheduledBackup
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = GlitchtipReconciler.SELECTOR)
class GlitchtipPostgresBackup : DependentPostgresScheduledBackup<Glitchtip>(Glitchtip.Postgres) {
    class ReconcilePrecondition : DependentPostgresScheduledBackup.ReconcilePrecondition<Glitchtip>()
}
