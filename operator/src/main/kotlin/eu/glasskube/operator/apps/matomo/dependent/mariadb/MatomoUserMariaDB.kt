package eu.glasskube.operator.apps.matomo.dependent.mariadb

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.apps.matomo.Matomo
import eu.glasskube.operator.apps.matomo.MatomoReconciler
import eu.glasskube.operator.apps.matomo.databaseUser
import eu.glasskube.operator.apps.matomo.mariaDBHost
import eu.glasskube.operator.apps.matomo.resourceLabels
import eu.glasskube.operator.apps.matomo.secretName
import eu.glasskube.operator.infra.mariadb.DatabasebRef
import eu.glasskube.operator.infra.mariadb.User
import eu.glasskube.operator.infra.mariadb.UserMariaDBSpec
import eu.glasskube.operator.infra.mariadb.userMariaDB
import io.fabric8.kubernetes.api.model.SecretKeySelector
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = MatomoReconciler.SELECTOR)
class MatomoUserMariaDB : CRUDKubernetesDependentResource<User, Matomo>(User::class.java) {
    override fun desired(primary: Matomo, context: Context<Matomo>) = userMariaDB {
        metadata {
            name = primary.databaseUser
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        spec = UserMariaDBSpec(
            mariaDbRef = DatabasebRef(primary.mariaDBHost),
            passwordSecretKeyRef = SecretKeySelector("MATOMO_DATABASE_PASSWORD", primary.secretName, null)
        )
    }
}
