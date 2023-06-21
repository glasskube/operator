package eu.glasskube.operator.apps.metabase

import eu.glasskube.operator.Labels
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
        const val APP_VERSION = "0.46.5"
    }
}

val Metabase.resourceLabels
    get() = Labels.resourceLabels(Metabase.APP_NAME, metadata.name, Metabase.APP_NAME, Metabase.APP_VERSION)
val Metabase.resourceLabelSelector
    get() = Labels.resourceLabelSelector(Metabase.APP_NAME, metadata.name, Metabase.APP_NAME)
val Metabase.genericResourceName get() = "${Metabase.APP_NAME}-${metadata.name}"
val Metabase.deploymentName get() = genericResourceName
val Metabase.secretName get() = genericResourceName
val Metabase.configMapName get() = genericResourceName
val Metabase.iniConfigMapName get() = "$genericResourceName-ini"
val Metabase.dbClusterName get() = "$genericResourceName-db"
val Metabase.ingressName get() = genericResourceName
val Metabase.httpServiceName get() = "$genericResourceName-http"
val Metabase.ingressTlsCertName get() = "$genericResourceName-cert"
