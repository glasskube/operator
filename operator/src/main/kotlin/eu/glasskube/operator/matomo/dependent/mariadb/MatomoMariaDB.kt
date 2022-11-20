package eu.glasskube.operator.matomo.dependent.mariadb

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
