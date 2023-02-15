package eu.glasskube.operator.matomo

import eu.glasskube.operator.resourceLabels
import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Plural
import io.fabric8.kubernetes.model.annotation.Version

data class MatomoSpec(
    val host: String? = null
)

class MatomoStatus

@Group("glasskube.eu")
@Version("v1alpha1")
@Plural("matomos")
class Matomo : CustomResource<MatomoSpec, MatomoStatus>(), Namespaced

val Matomo.identifyingLabel
    get() = MatomoReconciler.LABEL to metadata.name

val Matomo.resourceLabels
    get() = resourceLabels(MatomoReconciler.APP_NAME, identifyingLabel)

val Matomo.genericResourceName
    get() = "${MatomoReconciler.APP_NAME}-${metadata.name}"

val Matomo.deploymentName
    get() = genericResourceName

val Matomo.serviceName
    get() = genericResourceName

val Matomo.ingressName
    get() = genericResourceName

val Matomo.ingressTlsCertName
    get() = "$genericResourceName-cert"

val Matomo.configMapName
    get() = genericResourceName

val Matomo.persistentVolumeClaimName
    get() = genericResourceName

val Matomo.secretName
    get() = mariaDBHost

val Matomo.mariaDBHost
    get() = "$genericResourceName-mariadb"

val Matomo.databaseName
    get() = "matomo"

val Matomo.databaseUser
    get() = "matomo"
