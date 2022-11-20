package eu.glasskube.operator.matomo.dependent.mariadb

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.mariadb.*
import eu.glasskube.operator.matomo.*
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = MatomoReconciler.SELECTOR)
class MatomoUserMariaDB : CRUDKubernetesDependentResource<UserMariaDB, Matomo>(UserMariaDB::class.java) {
    override fun desired(primary: Matomo, context: Context<Matomo>) = userMariaDB {
        metadata {
            name = primary.databaseUser
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        spec = UserMariaDBSpec(
            mariaDbRef = DatabaseMariaDbRef(primary.mariaDBHost),
            passwordSecretKeyRef = MariaDBPasswordSecretKeyRef(primary.secretName, "MATOMO_DATABASE_PASSWORD")
        )
    }
}
