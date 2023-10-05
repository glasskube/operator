package eu.glasskube.operator.apps.metabase

import com.fasterxml.jackson.annotation.JsonIgnore
import eu.glasskube.operator.Labels
import eu.glasskube.operator.apps.common.backup.ResourceWithBackups
import eu.glasskube.operator.apps.common.database.ResourceWithDatabaseSpec
import eu.glasskube.operator.apps.common.database.postgres.PostgresDatabaseSpec
import eu.glasskube.operator.apps.metabase.Metabase.Postgres.postgresClusterLabelSelector
import eu.glasskube.operator.generic.dependent.backups.VeleroNameMapper
import eu.glasskube.operator.generic.dependent.postgres.PostgresNameMapper
import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Plural
import io.fabric8.kubernetes.model.annotation.Version

@Group("glasskube.eu")
@Version("v1alpha1")
@Plural("metabases")
class Metabase :
    CustomResource<MetabaseSpec, MetabaseStatus>(),
    Namespaced,
    ResourceWithBackups,
    ResourceWithDatabaseSpec<PostgresDatabaseSpec> {
    companion object {
        const val APP_NAME = "metabase"
    }

    object Postgres : PostgresNameMapper<Metabase>() {
        override fun getName(primary: Metabase) = "${primary.genericResourceName}-db"
        override fun getLabels(primary: Metabase) = primary.resourceLabels
        override fun getDatabaseName(primary: Metabase) = "metabase"
    }

    @delegate:JsonIgnore
    override val velero by lazy {
        object : VeleroNameMapper(this) {
            override val resourceName = genericResourceName
            override val resourceLabels = this@Metabase.resourceLabels
            override val labelSelectors = listOf(resourceLabelSelector, postgresClusterLabelSelector)
        }
    }
}

val Metabase.resourceLabels
    get() = Labels.resourceLabels(Metabase.APP_NAME, metadata.name, Metabase.APP_NAME, spec.version)
val Metabase.resourceLabelSelector
    get() = Labels.resourceLabelSelector(Metabase.APP_NAME, metadata.name, Metabase.APP_NAME)
val Metabase.genericResourceName get() = "${Metabase.APP_NAME}-${metadata.name}"
val Metabase.secretName get() = genericResourceName
val Metabase.configMapName get() = genericResourceName
val Metabase.ingressName get() = genericResourceName
val Metabase.httpServiceName get() = "$genericResourceName-http"
val Metabase.ingressTlsCertName get() = "$genericResourceName-cert"
