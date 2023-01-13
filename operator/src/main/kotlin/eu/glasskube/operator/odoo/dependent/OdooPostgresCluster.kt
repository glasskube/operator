package eu.glasskube.operator.odoo.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.config.ConfigKey
import eu.glasskube.operator.getConfig
import eu.glasskube.operator.odoo.Odoo
import eu.glasskube.operator.odoo.OdooReconciler
import eu.glasskube.operator.odoo.dbName
import eu.glasskube.operator.odoo.dbSecretName
import eu.glasskube.operator.odoo.dbSuperuserSecretName
import eu.glasskube.operator.odoo.resourceLabels
import eu.glasskube.operator.postgres.BootstrapConfiguration
import eu.glasskube.operator.postgres.BootstrapInitDB
import eu.glasskube.operator.postgres.Cluster
import eu.glasskube.operator.postgres.ClusterSpec
import eu.glasskube.operator.postgres.StorageConfiguration
import eu.glasskube.operator.postgres.postgresCluster
import io.fabric8.kubernetes.api.model.LocalObjectReference
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = OdooReconciler.SELECTOR)
class OdooPostgresCluster : CRUDKubernetesDependentResource<Cluster, Odoo>(Cluster::class.java) {
    override fun desired(primary: Odoo, context: Context<Odoo>) = postgresCluster {
        metadata {
            name = primary.dbName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        spec = ClusterSpec(
            instances = 1,
            superuserSecret = LocalObjectReference(primary.dbSuperuserSecretName),
            bootstrap = BootstrapConfiguration(
                initdb = BootstrapInitDB(
                    database = Odoo.dbName,
                    owner = Odoo.dbUsername,
                    secret = LocalObjectReference(primary.dbSecretName)
                )
            ),
            storage = StorageConfiguration(
                storageClass = getConfig(client, ConfigKey.databaseStorageClassName),
                size = "10Gi"
            )
        )
    }
}
