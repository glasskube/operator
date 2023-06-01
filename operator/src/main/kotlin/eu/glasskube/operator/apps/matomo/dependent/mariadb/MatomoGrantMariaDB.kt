package eu.glasskube.operator.apps.matomo.dependent.mariadb

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.apps.matomo.Matomo
import eu.glasskube.operator.apps.matomo.MatomoReconciler
import eu.glasskube.operator.apps.matomo.databaseName
import eu.glasskube.operator.apps.matomo.databaseUser
import eu.glasskube.operator.apps.matomo.mariaDBHost
import eu.glasskube.operator.apps.matomo.resourceLabels
import eu.glasskube.operator.infra.mariadb.DatabasebRef
import eu.glasskube.operator.infra.mariadb.Grant
import eu.glasskube.operator.infra.mariadb.GrantSpec
import eu.glasskube.operator.infra.mariadb.grantMariaDB
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = MatomoReconciler.SELECTOR)
class MatomoGrantMariaDB : CRUDKubernetesDependentResource<Grant, Matomo>(Grant::class.java) {

    override fun desired(primary: Matomo, context: Context<Matomo>) = grantMariaDB {
        metadata {
            name = primary.mariaDBHost
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        spec = GrantSpec(mariaDbRef = DatabasebRef(primary.mariaDBHost), database = primary.databaseName, username = primary.databaseUser)
    }
}
