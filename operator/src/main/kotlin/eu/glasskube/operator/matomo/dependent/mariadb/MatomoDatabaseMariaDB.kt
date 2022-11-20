package eu.glasskube.operator.matomo.dependent.mariadb

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.mariadb.DatabaseMariaDB
import eu.glasskube.operator.mariadb.DatabaseMariaDBSpec
import eu.glasskube.operator.mariadb.DatabaseMariaDbRef
import eu.glasskube.operator.mariadb.databaseMariaDB
import eu.glasskube.operator.matomo.*
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = MatomoReconciler.SELECTOR)
class MatomoDatabaseMariaDB : CRUDKubernetesDependentResource<DatabaseMariaDB, Matomo>(DatabaseMariaDB::class.java) {

    override fun desired(primary: Matomo, context: Context<Matomo>) = databaseMariaDB {
        metadata {
            name = primary.databaseName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        spec = DatabaseMariaDBSpec(
            mariaDbRef = DatabaseMariaDbRef(primary.mariaDBHost)
        )
    }
}
