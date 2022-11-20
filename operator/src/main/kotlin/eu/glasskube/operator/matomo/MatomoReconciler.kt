package eu.glasskube.operator.matomo

import eu.glasskube.operator.matomo.dependent.MatomoConfigMap
import eu.glasskube.operator.matomo.dependent.MatomoDeployment
import eu.glasskube.operator.matomo.dependent.MatomoIngress
import eu.glasskube.operator.matomo.dependent.MatomoSecret
import eu.glasskube.operator.matomo.dependent.MatomoService
import eu.glasskube.operator.matomo.dependent.mariadb.MatomoDatabaseMariaDB
import eu.glasskube.operator.matomo.dependent.mariadb.MatomoGrantMariaDB
import eu.glasskube.operator.matomo.dependent.mariadb.MatomoMariaDB
import eu.glasskube.operator.matomo.dependent.mariadb.MatomoUserMariaDB
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration
import io.javaoperatorsdk.operator.api.reconciler.Reconciler
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl
import io.javaoperatorsdk.operator.api.reconciler.dependent.Dependent

@ControllerConfiguration(
    dependents = [
        Dependent(type = MatomoDeployment::class),
        Dependent(type = MatomoSecret::class),
        Dependent(type = MatomoConfigMap::class),
        Dependent(type = MatomoService::class),
        Dependent(type = MatomoIngress::class),
        Dependent(type = MatomoMariaDB::class),
        Dependent(type = MatomoDatabaseMariaDB::class),
        Dependent(type = MatomoUserMariaDB::class),
        Dependent(type = MatomoGrantMariaDB::class),
    ]
)
class MatomoReconciler : Reconciler<Matomo> {
    override fun reconcile(resource: Matomo, context: Context<Matomo>): UpdateControl<Matomo> {
        resource.status = MatomoStatus()
        return UpdateControl.patchStatus(resource)
    }

    companion object {
        const val LABEL = "glasskube.eu/Matomo"
        const val APP_NAME = "matomo"
        const val SELECTOR = "app.kubernetes.io/managed-by=glasskube-operator,app=$APP_NAME"
    }
}
