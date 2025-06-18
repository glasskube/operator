package eu.glasskube.operator.apps.matomo

import com.fasterxml.jackson.annotation.JsonIgnore
import eu.glasskube.operator.apps.common.backup.BackupSpec
import eu.glasskube.operator.apps.common.backup.HasBackupSpec
import eu.glasskube.operator.apps.common.backup.ResourceWithBackups
import eu.glasskube.operator.apps.common.database.HasDatabaseSpec
import eu.glasskube.operator.apps.common.database.HasReadyStatus
import eu.glasskube.operator.apps.common.database.ResourceWithDatabaseSpec
import eu.glasskube.operator.apps.common.database.mariadb.MariaDbDatabaseSpec
import eu.glasskube.operator.apps.common.storage.GenericStorageSpec
import eu.glasskube.operator.generic.dependent.backups.VeleroNameMapper
import eu.glasskube.utils.resourceLabels
import io.fabric8.generator.annotation.Nullable
import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.api.model.Quantity
import io.fabric8.kubernetes.api.model.ResourceRequirements
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Plural
import io.fabric8.kubernetes.model.annotation.Version

data class MatomoSpec(
    val host: String? = null,
    @field:Nullable
    val smtp: MatomoSmtp? = null,
    val resources: ResourceRequirements = ResourceRequirements(
        null,
        mapOf("memory" to Quantity("600", "Mi")),
        mapOf("memory" to Quantity("300", "Mi"))
    ),
    val version: String = "4.15.1.1",
    override val database: MariaDbDatabaseSpec = MariaDbDatabaseSpec(),
    override val backups: BackupSpec?,
    val storage: GenericStorageSpec?,
) : HasBackupSpec, HasDatabaseSpec<MariaDbDatabaseSpec>

data class MatomoStatus(val readyReplicas: Int) : HasReadyStatus {
    override val isReady get() = readyReplicas > 0
}

@Group("glasskube.eu")
@Version("v1alpha1")
@Plural("matomos")
class Matomo :
    CustomResource<MatomoSpec, MatomoStatus>(),
    Namespaced,
    ResourceWithBackups,
    ResourceWithDatabaseSpec<MariaDbDatabaseSpec> {
    companion object {
        const val APP_NAME = "matomo"
    }

    @delegate:JsonIgnore
    override val velero by lazy {
        object : VeleroNameMapper(this) {
            override val resourceName = genericResourceName
            override val resourceLabels = this@Matomo.resourceLabels
            override val labelSelectors = listOf(this@Matomo.resourceLabels, mariaDbLabels)
        }
    }
}

internal val Matomo.identifyingLabel get() = MatomoReconciler.LABEL to metadata.name
internal val Matomo.resourceLabels get() = resourceLabels(Matomo.APP_NAME, identifyingLabel)
internal val Matomo.mariaDbLabels get() = mapOf(identifyingLabel)
internal val Matomo.genericResourceName get() = "${Matomo.APP_NAME}-${metadata.name}"
internal val Matomo.cronName get() = "$genericResourceName-cron"
internal val Matomo.volumeName get() = "$genericResourceName-data"
internal val Matomo.deploymentName get() = genericResourceName
internal val Matomo.serviceName get() = genericResourceName
internal val Matomo.ingressName get() = genericResourceName
internal val Matomo.ingressTlsCertName get() = "$genericResourceName-cert"
internal val Matomo.configMapName get() = genericResourceName
internal val Matomo.configSecretName get() = "$genericResourceName-config"
internal val Matomo.genericMariaDBName get() = "$genericResourceName-mariadb"
internal val Matomo.databaseSecretName get() = genericMariaDBName
internal val Matomo.mariaDBHost get() = genericMariaDBName
val Matomo.databaseName get() = "matomo"
val Matomo.databaseUser get() = "matomo"
internal val Matomo.appImage get() = "glasskube/${Matomo.APP_NAME}:${spec.version}"
