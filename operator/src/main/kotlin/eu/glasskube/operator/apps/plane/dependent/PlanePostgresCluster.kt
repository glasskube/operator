package eu.glasskube.operator.apps.plane.dependent

import eu.glasskube.operator.apps.plane.Plane
import eu.glasskube.operator.apps.plane.PlaneReconciler
import eu.glasskube.operator.config.ConfigService
import eu.glasskube.operator.generic.condition.PostgresReadyCondition
import eu.glasskube.operator.generic.dependent.postgres.DependentPostgresCluster
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = PlaneReconciler.SELECTOR)
class PlanePostgresCluster(configService: ConfigService) :
    DependentPostgresCluster<Plane>(Plane.Postgres, configService) {

    class ReadyCondition : PostgresReadyCondition<Plane>()

    override val Plane.defaultStorageSize get() = "10Gi"
}
