package eu.glasskube.operator.apps.metabase

import eu.glasskube.operator.Labels
import eu.glasskube.operator.generic.dependent.postgres.PostgresNameMapper
import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Plural
import io.fabric8.kubernetes.model.annotation.Version

@Group("glasskube.eu")
@Version("v1alpha1")
@Plural("metabases")
class Metabase : CustomResource<MetabaseSpec, MetabaseStatus>(), Namespaced {
    companion object {
        const val APP_NAME = "metabase"
        const val APP_VERSION = "0.46.6.1"
    }

    object Postgres : PostgresNameMapper<Metabase>() {
        override fun getName(primary: Metabase) = "${primary.genericResourceName}-db"
        override fun getLabels(primary: Metabase) = primary.resourceLabels
        override fun getDatabaseName(primary: Metabase) = "metabase"
    }
}

val Metabase.resourceLabels
    get() = Labels.resourceLabels(Metabase.APP_NAME, metadata.name, Metabase.APP_NAME, Metabase.APP_VERSION)
val Metabase.resourceLabelSelector
    get() = Labels.resourceLabelSelector(Metabase.APP_NAME, metadata.name, Metabase.APP_NAME)
val Metabase.genericResourceName get() = "${Metabase.APP_NAME}-${metadata.name}"
val Metabase.secretName get() = genericResourceName
val Metabase.configMapName get() = genericResourceName
val Metabase.ingressName get() = genericResourceName
val Metabase.httpServiceName get() = "$genericResourceName-http"
val Metabase.ingressTlsCertName get() = "$genericResourceName-cert"
