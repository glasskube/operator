package eu.glasskube.operator.odoo

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import eu.glasskube.operator.resourceLabels
import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Plural
import io.fabric8.kubernetes.model.annotation.Version

data class OdooSpec @JsonCreator constructor(
    @JsonProperty("host")
    val host: String
)

class OdooStatus

@Group("glasskube.eu")
@Version("v1alpha1")
@Plural("odoos")
class Odoo : CustomResource<OdooSpec, OdooStatus>(), Namespaced {
    companion object {
        const val volumeName = "web-data"
        const val volumePath = "/var/lib/odoo"
        const val configMapName = "config-data"
        const val configFile = "odoo.conf"
        const val configPath = "/etc/odoo"
        const val dbName = "odoo"
        const val dbUsername = "odoo"
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

val Odoo.dbName
    get() = "$genericResourceName-db"

val Odoo.dbSecretName
    get() = "$genericResourceName-db"

val Odoo.dbSuperuserSecretName
    get() = "$genericResourceName-db-superuser"

val Odoo.serviceName
    get() = genericResourceName

val Odoo.ingressName
    get() = genericResourceName

val Odoo.ingressTlsCertName
    get() = "$genericResourceName-cert"
