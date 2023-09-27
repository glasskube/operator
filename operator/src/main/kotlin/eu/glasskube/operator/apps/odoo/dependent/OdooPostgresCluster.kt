package eu.glasskube.operator.apps.odoo.dependent

import eu.glasskube.operator.apps.odoo.Odoo
import eu.glasskube.operator.apps.odoo.OdooReconciler
import eu.glasskube.operator.apps.odoo.bucketName
import eu.glasskube.operator.apps.odoo.dbBackupSecretName
import eu.glasskube.operator.config.ConfigKey
import eu.glasskube.operator.config.ConfigService
import eu.glasskube.operator.generic.dependent.postgres.DependentPostgresCluster
import eu.glasskube.operator.generic.dependent.postgres.backup.bucketinfo.MinioBucketInfo
import eu.glasskube.operator.generic.dependent.postgres.backup.bucketinfo.MinioBucketInfoProvider
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = OdooReconciler.SELECTOR)
class OdooPostgresCluster(private val configService: ConfigService) :
    DependentPostgresCluster<Odoo>(Odoo.Postgres, configService) {
    override val Odoo.storageSize get() = "10Gi"
    override val Odoo.storageClass get() = configService[ConfigKey.databaseStorageClassName]
    override val Odoo.defaultBackupRetentionPolicy get() = "15d"
    override val backupBucketInfoProvider = MinioBucketInfoProvider<Odoo> { primary, _ ->
        MinioBucketInfo(primary.bucketName, primary.dbBackupSecretName)
    }
}
