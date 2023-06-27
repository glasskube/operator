package eu.glasskube.operator.apps.matomo.dependent.mariadb

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.apps.matomo.Matomo
import eu.glasskube.operator.apps.matomo.MatomoReconciler
import eu.glasskube.operator.apps.matomo.databaseSecretName
import eu.glasskube.operator.apps.matomo.mariaDBHost
import eu.glasskube.operator.apps.matomo.resourceLabels
import eu.glasskube.operator.config.ConfigKey
import eu.glasskube.operator.config.ConfigService
import eu.glasskube.operator.infra.mariadb.Exporter
import eu.glasskube.operator.infra.mariadb.MariaDB
import eu.glasskube.operator.infra.mariadb.MariaDBImage
import eu.glasskube.operator.infra.mariadb.MariaDBResources
import eu.glasskube.operator.infra.mariadb.MariaDBResourcesRequest
import eu.glasskube.operator.infra.mariadb.MariaDBSpec
import eu.glasskube.operator.infra.mariadb.MariaDBVolumeClaimTemplate
import eu.glasskube.operator.infra.mariadb.Metrics
import eu.glasskube.operator.infra.mariadb.ServiceMonitor
import eu.glasskube.operator.infra.mariadb.mariaDB
import io.fabric8.kubernetes.api.model.Quantity
import io.fabric8.kubernetes.api.model.ResourceRequirements
import io.fabric8.kubernetes.api.model.SecretKeySelector
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.dependent.DependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.dependent.workflow.Condition
import kotlin.jvm.optionals.getOrNull

@KubernetesDependent(labelSelector = MatomoReconciler.SELECTOR)
class MatomoMariaDB(private val configService: ConfigService) :
    CRUDKubernetesDependentResource<MariaDB, Matomo>(MariaDB::class.java) {

    class ReadyPostCondition : Condition<MariaDB, Matomo> {
        override fun isMet(
            dependentResource: DependentResource<MariaDB, Matomo>?,
            primary: Matomo?,
            context: Context<Matomo>?
        ): Boolean =
            dependentResource?.getSecondaryResource(primary, context)
                ?.getOrNull()
                ?.status?.conditions?.firstOrNull()
                ?.run { type == "Ready" && status == "True" }
                ?: false
    }

    override fun desired(primary: Matomo, context: Context<Matomo>) = mariaDB {
        metadata {
            name = primary.mariaDBHost
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        spec = MariaDBSpec(
            rootPasswordSecretKeyRef = SecretKeySelector("ROOT_DATABASE_PASSWORD", primary.databaseSecretName, null),
            image = MariaDBImage("mariadb", "10.7.4", "IfNotPresent"),
            volumeClaimTemplate = MariaDBVolumeClaimTemplate(
                resources = MariaDBResources(MariaDBResourcesRequest("10Gi")),
                storageClassName = configService.getValue(ConfigKey.databaseStorageClassName)
            ),
            resources = ResourceRequirements(
                null,
                mapOf("memory" to Quantity("512", "Mi")),
                mapOf("memory" to Quantity("256", "Mi"))
            ),
            metrics = Metrics(
                exporter = Exporter(
                    image = MariaDBImage("prom/mysqld-exporter", "v0.14.0", "IfNotPresent")
                ),
                serviceMonitor = ServiceMonitor(
                    prometheusRelease = "kube-prometheus-stack"
                )
            )
        )
    }
}
