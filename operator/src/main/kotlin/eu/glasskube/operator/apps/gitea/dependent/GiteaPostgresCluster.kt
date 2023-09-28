package eu.glasskube.operator.apps.gitea.dependent

import eu.glasskube.operator.apps.gitea.Gitea
import eu.glasskube.operator.apps.gitea.GiteaReconciler
import eu.glasskube.operator.config.ConfigService
import eu.glasskube.operator.generic.condition.PostgresReadyCondition
import eu.glasskube.operator.generic.dependent.postgres.DependentPostgresCluster
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = GiteaReconciler.SELECTOR)
class GiteaPostgresCluster(configService: ConfigService) :
    DependentPostgresCluster<Gitea>(Gitea.Postgres, configService) {
    class ReadyPostCondition : PostgresReadyCondition<Gitea>()
    override val Gitea.defaultStorageSize get() = "20Gi"
}
