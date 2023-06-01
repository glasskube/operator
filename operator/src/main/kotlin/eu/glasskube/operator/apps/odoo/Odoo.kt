package eu.glasskube.operator.apps.odoo

import eu.glasskube.operator.resourceLabels
import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Plural
import io.fabric8.kubernetes.model.annotation.Version

data class OdooSpec(
    val host: String,
    val demoEnabled: Boolean = true
)

data class OdooStatus(
    val ready: Boolean = false,
    val demoEnabledOnInstall: Boolean? = null
)

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
    get() = "$dbName-app"

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
