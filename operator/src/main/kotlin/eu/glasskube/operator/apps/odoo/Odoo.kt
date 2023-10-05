package eu.glasskube.operator.apps.odoo

import com.fasterxml.jackson.annotation.JsonIgnore
import eu.glasskube.operator.apps.common.backup.BackupSpec
import eu.glasskube.operator.apps.common.backup.HasBackupSpec
import eu.glasskube.operator.apps.common.backup.ResourceWithBackups
import eu.glasskube.operator.apps.common.database.HasDatabaseSpec
import eu.glasskube.operator.apps.common.database.HasReadyStatus
import eu.glasskube.operator.apps.common.database.ResourceWithDatabaseSpec
import eu.glasskube.operator.apps.common.database.postgres.PostgresDatabaseSpec
import eu.glasskube.operator.apps.odoo.Odoo.Postgres.postgresClusterLabelSelector
import eu.glasskube.operator.generic.dependent.backups.VeleroNameMapper
import eu.glasskube.operator.generic.dependent.postgres.PostgresNameMapper
import eu.glasskube.operator.validation.Patterns
import eu.glasskube.utils.resourceLabels
import io.fabric8.generator.annotation.Nullable
import io.fabric8.generator.annotation.Pattern
import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.api.model.ResourceRequirements
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Plural
import io.fabric8.kubernetes.model.annotation.Version

data class OdooSpec(
    val host: String,
    val demoEnabled: Boolean = true,
    val resources: ResourceRequirements = ResourceRequirements(),
    @field:Pattern(Patterns.SEMVER)
    val version: String = "16.0.20230901",
    @field:Nullable
    override val database: PostgresDatabaseSpec = PostgresDatabaseSpec(),
    override val backups: BackupSpec?
) : HasBackupSpec, HasDatabaseSpec<PostgresDatabaseSpec>

data class OdooStatus(
    val ready: Boolean = false,
    val demoEnabledOnInstall: Boolean? = null
) : HasReadyStatus {
    override val isReady get() = ready
}

@Group("glasskube.eu")
@Version("v1alpha1")
@Plural("odoos")
class Odoo :
    CustomResource<OdooSpec, OdooStatus>(),
    Namespaced,
    ResourceWithBackups,
    ResourceWithDatabaseSpec<PostgresDatabaseSpec> {
    internal companion object {
        const val APP_NAME = "odoo"

        const val volumeName = "web-data"
        const val volumePath = "/var/lib/odoo"
        const val configMapName = "config-data"
        const val configFile = "odoo.conf"
        const val configPath = "/etc/odoo"
    }

    object Postgres : PostgresNameMapper<Odoo>() {
        override fun getName(primary: Odoo) = "${primary.genericResourceName}-db"
        override fun getLabels(primary: Odoo) = primary.resourceLabels
        override fun getDatabaseName(primary: Odoo) = "odoo"
    }

    @delegate:JsonIgnore
    override val velero by lazy {
        object : VeleroNameMapper(this) {
            override val resourceName = genericResourceName
            override val resourceLabels = this@Odoo.resourceLabels
            override val labelSelectors = listOf(this@Odoo.resourceLabels, postgresClusterLabelSelector)
        }
    }
}

val Odoo.identifyingLabel
    get() = OdooReconciler.LABEL to metadata.name

val Odoo.resourceLabels
    get() = resourceLabels(OdooReconciler.APP_NAME, identifyingLabel)

val Odoo.genericResourceName
    get() = "${OdooReconciler.APP_NAME}-${metadata.name}"

val Odoo.deploymentName
    get() = genericResourceName

val Odoo.volumeName
    get() = "$genericResourceName-web-data"

val Odoo.configMapName
    get() = genericResourceName

val Odoo.serviceName
    get() = genericResourceName

val Odoo.ingressName
    get() = genericResourceName

val Odoo.ingressTlsCertName
    get() = "$genericResourceName-cert"

val Odoo.dbBackupSecretName
    get() = "$genericResourceName-backup"

val Odoo.dbBackupUsername
    get() = dbBackupSecretName
val Odoo.bucketName
    get() = "$genericResourceName-${metadata.namespace}-backup"
