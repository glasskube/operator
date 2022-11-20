package eu.glasskube.operator.matomo.dependent.mariadb

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.mariadb.MariaDB
import eu.glasskube.operator.mariadb.MariaDBImage
import eu.glasskube.operator.mariadb.MariaDBPasswordSecretKeyRef
import eu.glasskube.operator.mariadb.MariaDBResources
import eu.glasskube.operator.mariadb.MariaDBResourcesRequest
import eu.glasskube.operator.mariadb.MariaDBSpec
import eu.glasskube.operator.mariadb.MariaDBVolumeClaimTemplate
import eu.glasskube.operator.mariadb.mariaDB
import eu.glasskube.operator.matomo.Matomo
import eu.glasskube.operator.matomo.MatomoReconciler
import eu.glasskube.operator.matomo.mariaDBHost
import eu.glasskube.operator.matomo.resourceLabels
import eu.glasskube.operator.matomo.secretName
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = MatomoReconciler.SELECTOR)
class MatomoMariaDB : CRUDKubernetesDependentResource<MariaDB, Matomo>(MariaDB::class.java) {

    override fun desired(primary: Matomo, context: Context<Matomo>) = mariaDB {
        metadata {
            name = primary.mariaDBHost
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        spec = MariaDBSpec(
            rootPasswordSecretKeyRef = MariaDBPasswordSecretKeyRef(primary.secretName, "ROOT_DATABASE_PASSWORD"),
            image = MariaDBImage("mariadb", "10.7.4", "IfNotPresent"),
            volumeClaimTemplate = MariaDBVolumeClaimTemplate(
                MariaDBResources(MariaDBResourcesRequest("100Mi")),
            )
        )
    }
}
