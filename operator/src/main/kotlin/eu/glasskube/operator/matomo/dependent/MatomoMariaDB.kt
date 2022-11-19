package eu.glasskube.operator.matomo.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.mariadb.*
import eu.glasskube.operator.matomo.*
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = MatomoReconciler.SELECTOR)
class MatomoMariaDB : CRUDKubernetesDependentResource<MariaDB, Matomo>(MariaDB::class.java) {

    override fun desired(primary: Matomo, context: Context<Matomo>) = mariaDB {
        metadata {
            name = primary.mariaDBName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        spec = MariaDBSpec(
            MariaDBPasswordSecretKeyRef(primary.secretName, "MATOMO_DATABASE_PASSWORD"),
            MariaDBImage("mariadb", "10.7.4", "IfNotPresent"),
            3306,
            MariaDBVolumeClaimTemplate(
                MariaDBResources(MariaDBResourcesRequest("100Mi")),
                "standard",
                listOf("ReadWriteOnce")
            )
        )
    }

}


@KubernetesDependent(labelSelector = MatomoReconciler.SELECTOR)
class MatomoDatabaseMariaDB : CRUDKubernetesDependentResource<DatabaseMariaDB, Matomo>(DatabaseMariaDB::class.java) {
    override fun desired(primary: Matomo, context: Context<Matomo>) = databaseMariaDB {
        metadata {
            name = "mariadb"
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        spec = DatabaseMariaDBSpec(
            DatabaseMariaDbRef("mariadb"),
            "utf8",
            "utf8_general_ci"
        )
    }

}

@KubernetesDependent(labelSelector = MatomoReconciler.SELECTOR)
class MatomoGrantMariaDB : CRUDKubernetesDependentResource<GrantMariaDB, Matomo>(GrantMariaDB::class.java) {
    override fun desired(primary: Matomo, context: Context<Matomo>) = grantMariaDB {
        metadata {
            name = "user"
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        spec = GrantMariaDBSpec(
            DatabaseMariaDbRef("mariadb"),
            listOf("ALL"),
            "*",
            "*",
            "user",
            true
        )
    }

}

@KubernetesDependent(labelSelector = MatomoReconciler.SELECTOR)
class MatomoUserMariaDB : CRUDKubernetesDependentResource<UserMariaDB, Matomo>(UserMariaDB::class.java) {
    override fun desired(primary: Matomo, context: Context<Matomo>) = userMariaDB {
        metadata {
            name = primary.mariaDBName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        spec = UserMariaDBSpec(
            DatabaseMariaDbRef(primary.mariaDBName),
            MariaDBPasswordSecretKeyRef(primary.secretName, "MATOMO_DATABASE_PASSWORD"),
            20
        )
    }

}

