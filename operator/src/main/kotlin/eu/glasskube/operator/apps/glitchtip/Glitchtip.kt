package eu.glasskube.operator.apps.glitchtip

import eu.glasskube.operator.Labels
import eu.glasskube.operator.generic.dependent.postgres.PostgresNameMapper
import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Plural
import io.fabric8.kubernetes.model.annotation.Version

@Group("glasskube.eu")
@Version("v1alpha1")
@Plural("glitchtips")
class Glitchtip : CustomResource<GlitchtipSpec, GlitchtipStatus>(), Namespaced {
    companion object {
        const val APP_NAME = "glitchtip"
        const val APP_VERSION = "0.46.5"
    }

    object Postgres : PostgresNameMapper<Glitchtip>() {
        override fun getName(primary: Glitchtip) = "${primary.genericResourceName}-db"
        override fun getLabels(primary: Glitchtip) = primary.resourceLabels
        override fun getDatabaseName(primary: Glitchtip) = "glitchtip"
    }
}

val Glitchtip.resourceLabels
    get() = Labels.resourceLabels(Glitchtip.APP_NAME, metadata.name, Glitchtip.APP_NAME, Glitchtip.APP_VERSION)
val Glitchtip.resourceLabelSelector
    get() = Labels.resourceLabelSelector(Glitchtip.APP_NAME, metadata.name, Glitchtip.APP_NAME)
val Glitchtip.genericResourceName get() = "${Glitchtip.APP_NAME}-${metadata.name}"
val Glitchtip.secretName get() = genericResourceName
val Glitchtip.configMapName get() = genericResourceName
val Glitchtip.ingressName get() = genericResourceName
val Glitchtip.httpServiceName get() = "$genericResourceName-http"
val Glitchtip.ingressTlsCertName get() = "$genericResourceName-cert"
