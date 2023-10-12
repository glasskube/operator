package eu.glasskube.operator.apps.metabase.dependent

import eu.glasskube.operator.apps.metabase.Metabase
import eu.glasskube.operator.apps.metabase.MetabaseReconciler
import eu.glasskube.operator.generic.dependent.postgres.DependentPostgresScheduledBackup
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = MetabaseReconciler.SELECTOR)
class MetabasePostgresBackup : DependentPostgresScheduledBackup<Metabase>(Metabase.Postgres) {
    class ReconcilePrecondition : DependentPostgresScheduledBackup.ReconcilePrecondition<Metabase>()
}
