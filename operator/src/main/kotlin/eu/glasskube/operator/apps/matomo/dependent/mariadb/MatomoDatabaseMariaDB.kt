package eu.glasskube.operator.apps.matomo.dependent.mariadb

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.apps.matomo.Matomo
import eu.glasskube.operator.apps.matomo.MatomoReconciler
import eu.glasskube.operator.apps.matomo.databaseName
import eu.glasskube.operator.apps.matomo.mariaDBHost
import eu.glasskube.operator.apps.matomo.resourceLabels
import eu.glasskube.operator.infra.mariadb.Database
import eu.glasskube.operator.infra.mariadb.DatabaseSpec
import eu.glasskube.operator.infra.mariadb.DatabasebRef
import eu.glasskube.operator.infra.mariadb.databaseMariaDB
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = MatomoReconciler.SELECTOR)
class MatomoDatabaseMariaDB : CRUDKubernetesDependentResource<Database, Matomo>(Database::class.java) {

    override fun desired(primary: Matomo, context: Context<Matomo>) = databaseMariaDB {
        metadata {
            name = primary.databaseName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        spec = DatabaseSpec(
            mariaDbRef = DatabasebRef(primary.mariaDBHost)
        )
    }
}
