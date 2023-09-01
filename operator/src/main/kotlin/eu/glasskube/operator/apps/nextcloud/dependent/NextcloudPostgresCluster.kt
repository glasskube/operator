package eu.glasskube.operator.apps.nextcloud.dependent

import eu.glasskube.operator.apps.nextcloud.Nextcloud
import eu.glasskube.operator.apps.nextcloud.NextcloudReconciler
import eu.glasskube.operator.config.ConfigService
import eu.glasskube.operator.generic.condition.PostgresReadyCondition
import eu.glasskube.operator.generic.dependent.postgres.DependentPostgresCluster
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = NextcloudReconciler.SELECTOR)
class NextcloudPostgresCluster(configService: ConfigService) : DependentPostgresCluster<Nextcloud>(Nextcloud.Postgres, configService) {
    class ReadyPostCondition : PostgresReadyCondition<Nextcloud>()
    override val Nextcloud.storageSize get() = "10Gi"
}
