package eu.glasskube.operator.matomo.dependent.mariadb

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.operator.config.ConfigKey
import eu.glasskube.operator.config.ConfigService
import eu.glasskube.operator.mariadb.Exporter
import eu.glasskube.operator.mariadb.MariaDB
import eu.glasskube.operator.mariadb.MariaDBImage
import eu.glasskube.operator.mariadb.MariaDBResources
import eu.glasskube.operator.mariadb.MariaDBResourcesRequest
import eu.glasskube.operator.mariadb.MariaDBSpec
import eu.glasskube.operator.mariadb.MariaDBVolumeClaimTemplate
import eu.glasskube.operator.mariadb.Metrics
import eu.glasskube.operator.mariadb.ServiceMonitor
import eu.glasskube.operator.mariadb.mariaDB
import eu.glasskube.operator.matomo.Matomo
import eu.glasskube.operator.matomo.MatomoReconciler
import eu.glasskube.operator.matomo.mariaDBHost
import eu.glasskube.operator.matomo.resourceLabels
import eu.glasskube.operator.matomo.secretName
import io.fabric8.kubernetes.api.model.SecretKeySelector
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = MatomoReconciler.SELECTOR)
class MatomoMariaDB(private val configService: ConfigService) :
    CRUDKubernetesDependentResource<MariaDB, Matomo>(MariaDB::class.java) {

    override fun desired(primary: Matomo, context: Context<Matomo>) = mariaDB {
        metadata {
            name = primary.mariaDBHost
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        spec = MariaDBSpec(
            rootPasswordSecretKeyRef = SecretKeySelector("ROOT_DATABASE_PASSWORD", primary.secretName, null),
            image = MariaDBImage("mariadb", "10.7.4", "IfNotPresent"),
            volumeClaimTemplate = MariaDBVolumeClaimTemplate(
                resources = MariaDBResources(MariaDBResourcesRequest("10Gi")),
                storageClassName = configService.getValue(ConfigKey.databaseStorageClassName)
            ),
            metrics = Metrics(
                exporter = Exporter(
                    image = MariaDBImage("prom/mysqld-exporter", "v0.14.0")
                ),
                serviceMonitor = ServiceMonitor(
                    prometheusRelease = "kube-prometheus-stack"
                )
            )
        )
    }
}
