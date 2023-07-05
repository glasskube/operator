package eu.glasskube.operator.apps.odoo.dependent

import eu.glasskube.operator.apps.odoo.Odoo
import eu.glasskube.operator.apps.odoo.OdooReconciler
import eu.glasskube.operator.apps.odoo.bucketName
import eu.glasskube.operator.apps.odoo.dbBackupSecretName
import eu.glasskube.operator.config.ConfigKey
import eu.glasskube.operator.config.ConfigService
import eu.glasskube.operator.generic.dependent.postgres.DependentPostgresCluster
import eu.glasskube.operator.generic.dependent.postgres.PostgresBackupInfo
import eu.glasskube.operator.generic.dependent.postgres.PostgresBackupInfoProvider
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = OdooReconciler.SELECTOR)
class OdooPostgresCluster(private val configService: ConfigService) : DependentPostgresCluster<Odoo>(Odoo.Postgres) {
    override val Odoo.storageSize get() = "10Gi"
    override val Odoo.storageClass get() = configService[ConfigKey.databaseStorageClassName]
    override val Odoo.backupRetentionPolicy get() = "15d"
    override val backupInfoProvider = PostgresBackupInfoProvider<Odoo> { primary, _ ->
        PostgresBackupInfo(primary.bucketName, primary.dbBackupSecretName)
    }
}
