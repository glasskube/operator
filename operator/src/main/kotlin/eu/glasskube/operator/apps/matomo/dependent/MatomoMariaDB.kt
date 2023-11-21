package eu.glasskube.operator.apps.matomo.dependent

import eu.glasskube.kubernetes.api.model.loggingId
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.kubernetes.api.model.objectMeta
import eu.glasskube.kubernetes.api.model.secretKeySelector
import eu.glasskube.kubernetes.api.model.toQuantity
import eu.glasskube.operator.apps.matomo.Matomo
import eu.glasskube.operator.apps.matomo.MatomoReconciler
import eu.glasskube.operator.apps.matomo.databaseName
import eu.glasskube.operator.apps.matomo.databaseSecretName
import eu.glasskube.operator.apps.matomo.databaseUser
import eu.glasskube.operator.apps.matomo.genericMariaDBName
import eu.glasskube.operator.apps.matomo.mariaDbLabels
import eu.glasskube.operator.apps.matomo.resourceLabels
import eu.glasskube.operator.config.ConfigKey
import eu.glasskube.operator.config.ConfigService
import eu.glasskube.operator.generic.condition.MariaDBReadyCondition
import eu.glasskube.operator.infra.mariadb.Exporter
import eu.glasskube.operator.infra.mariadb.MariaDB
import eu.glasskube.operator.infra.mariadb.MariaDBResources
import eu.glasskube.operator.infra.mariadb.MariaDBResourcesRequest
import eu.glasskube.operator.infra.mariadb.MariaDBSpec
import eu.glasskube.operator.infra.mariadb.MariaDBVolumeClaimTemplate
import eu.glasskube.operator.infra.mariadb.Metrics
import eu.glasskube.operator.infra.mariadb.ServiceMonitor
import eu.glasskube.operator.infra.mariadb.mariaDB
import eu.glasskube.utils.logger
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim
import io.fabric8.kubernetes.api.model.Quantity
import io.fabric8.kubernetes.api.model.ResourceRequirements
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = MatomoReconciler.SELECTOR)
class MatomoMariaDB(private val configService: ConfigService) :
    CRUDKubernetesDependentResource<MariaDB, Matomo>(MariaDB::class.java) {

    class ReadyPostCondition : MariaDBReadyCondition<Matomo>()

    private val defaultStorageSize = "10Gi"
    private val defaultStorageClass get() = configService.getValue(ConfigKey.databaseStorageClassName)

    private val Matomo.storageSize get() = spec.database.storage?.size ?: defaultStorageSize
    private val Matomo.storageClass get() = spec.database.storage?.storageClass ?: defaultStorageClass

    private val MariaDB.storageSize get() = spec.volumeClaimTemplate.resources.requests?.storage
    private fun getPersistentVolumeClaim(actual: MariaDB, context: Context<Matomo>): PersistentVolumeClaim? =
        context.client.persistentVolumeClaims().inNamespace(actual.namespace)
            .withName("storage-${actual.metadata.name}-0").get()

    override fun desired(primary: Matomo, context: Context<Matomo>) = mariaDB {
        metadata {
            name(primary.genericMariaDBName)
            namespace(primary.metadata.namespace)
            labels(primary.resourceLabels)
        }
        spec = MariaDBSpec(
            rootPasswordSecretKeyRef = secretKeySelector(primary.databaseSecretName, ROOT_DATABASE_PASSWORD),
            image = "mariadb:10.7.4",
            database = primary.databaseName,
            username = primary.databaseUser,
            passwordSecretKeyRef = secretKeySelector(primary.databaseSecretName, MATOMO_DATABASE_PASSWORD),
            volumeClaimTemplate = MariaDBVolumeClaimTemplate(
                resources = MariaDBResources(MariaDBResourcesRequest(primary.storageSize)),
                storageClassName = primary.storageClass
            ),
            resources = ResourceRequirements(
                null,
                mapOf("memory" to Quantity("512", "Mi")),
                mapOf("memory" to Quantity("256", "Mi"))
            ),
            metrics = Metrics(
                exporter = Exporter(
                    image = "prom/mysqld-exporter:v0.14.0"
                ),
                serviceMonitor = ServiceMonitor(
                    prometheusRelease = "kube-prometheus-stack"
                )
            ),
            inheritMetadata = objectMeta {
                labels(primary.mariaDbLabels)
                annotations(configService.getBackupAnnotations(primary, "storage"))
            },
            myCnf = """
                [mariadb]
                max_allowed_packet=256M
            """.trimIndent()
        )
    }

    override fun handleUpdate(actual: MariaDB, desired: MariaDB, primary: Matomo, context: Context<Matomo>): MariaDB =
        if (desired.storageSize != actual.storageSize) {
            // Do not update volumeClaimTemplate if the database has already been created. This would lead to an error,
            // because this field is immutable. To handle expansion of the PersistentVolumeClaim, we update the claim
            // directly, then recreate the MariaDB resource, so that the storage size is consistent with the claim.
            log.info(
                "storage request for ${primary.loggingId} has changed " +
                    "(actual: ${actual.storageSize}, desired: ${desired.storageSize}). " +
                    "Now updating the PersistentVolumeClaim and recreating MariaDB"
            )
            updateVolumeClaimStorageRequest(actual, primary, context)
            recreate(actual, desired, primary, context)
        } else if (desired.spec.inheritMetadata != actual.spec.inheritMetadata || desired.spec.myCnf != actual.spec.myCnf) {
            log.info(
                "MariaDB {} spec changed but cannot be applied through update. will be recreated",
                primary.loggingId
            )
            recreate(actual, desired, primary, context)
        } else {
            super.handleUpdate(actual, desired, primary, context)
        }

    private fun updateVolumeClaimStorageRequest(actual: MariaDB, primary: Matomo, context: Context<Matomo>) {
        getPersistentVolumeClaim(actual, context)
            ?.apply { spec.resources.requests["storage"] = primary.storageSize.toQuantity() }
            ?.let { context.client.resource(it) }
            ?.update()
    }

    private fun recreate(actual: MariaDB, desired: MariaDB, primary: Matomo, context: Context<Matomo>): MariaDB {
        context.client.resource(actual).delete()
        Thread.sleep(1000)
        return handleCreate(desired, primary, context)
            .also { log.info("MariaDB for ${primary.loggingId} recreated") }
    }

    companion object {
        private val log = logger()
        internal const val ROOT_DATABASE_PASSWORD = "ROOT_DATABASE_PASSWORD"
        internal const val MATOMO_DATABASE_PASSWORD = "MATOMO_DATABASE_PASSWORD"
    }
}
