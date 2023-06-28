package eu.glasskube.operator.apps.matomo

import eu.glasskube.operator.resourceLabels
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
    )
)

class MatomoStatus {
    override fun equals(other: Any?) = this === other || javaClass == other?.javaClass
    override fun hashCode() = javaClass.hashCode()
}

@Group("glasskube.eu")
@Version("v1alpha1")
@Plural("matomos")
class Matomo : CustomResource<MatomoSpec, MatomoStatus>(), Namespaced

val Matomo.identifyingLabel get() = MatomoReconciler.LABEL to metadata.name
val Matomo.resourceLabels get() = resourceLabels(MatomoReconciler.APP_NAME, identifyingLabel)
val Matomo.genericResourceName get() = "${MatomoReconciler.APP_NAME}-${metadata.name}"
val Matomo.deploymentName get() = genericResourceName
val Matomo.serviceName get() = genericResourceName
val Matomo.ingressName get() = genericResourceName
val Matomo.ingressTlsCertName get() = "$genericResourceName-cert"
val Matomo.configMapName get() = genericResourceName
val Matomo.configSecretName get() = "$genericResourceName-config"
val Matomo.genericMariaDBName get() = "$genericResourceName-mariadb"
val Matomo.databaseSecretName get() = genericMariaDBName
val Matomo.mariaDBHost get() = genericMariaDBName
val Matomo.persistentVolumeClaimName get() = "$genericResourceName-misc"
val Matomo.databaseName get() = "matomo"
val Matomo.databaseUser get() = "matomo"
