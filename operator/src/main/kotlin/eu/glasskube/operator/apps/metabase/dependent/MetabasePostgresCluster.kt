package eu.glasskube.operator.apps.metabase.dependent

import eu.glasskube.operator.apps.metabase.Metabase
import eu.glasskube.operator.apps.metabase.MetabaseReconciler
import eu.glasskube.operator.config.ConfigService
import eu.glasskube.operator.generic.condition.PostgresReadyCondition
import eu.glasskube.operator.generic.dependent.postgres.DependentPostgresCluster
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = MetabaseReconciler.SELECTOR)
class MetabasePostgresCluster(configService: ConfigService) :
    DependentPostgresCluster<Metabase>(Metabase.Postgres, configService) {
    class ReadyPostCondition : PostgresReadyCondition<Metabase>()

    override val Metabase.storageSize get() = "5Gi"
}
