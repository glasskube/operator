package eu.glasskube.operator.apps.gitlab.dependent

import eu.glasskube.operator.apps.gitlab.Gitlab
import eu.glasskube.operator.apps.gitlab.GitlabReconciler
import eu.glasskube.operator.config.ConfigService
import eu.glasskube.operator.generic.condition.PostgresReadyCondition
import eu.glasskube.operator.generic.dependent.postgres.DependentPostgresCluster
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = GitlabReconciler.SELECTOR)
class GitlabPostgresCluster(configService: ConfigService) :
    DependentPostgresCluster<Gitlab>(Gitlab.Postgres, configService) {
    class ReadyPostCondition : PostgresReadyCondition<Gitlab>()

    override val Gitlab.databaseOwnerName get() = "gitlab"
    override val Gitlab.storageSize get() = "20Gi"
}
