package eu.glasskube.operator.apps.matomo.dependent

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.kubernetes.api.model.secret
import eu.glasskube.operator.apps.matomo.Matomo
import eu.glasskube.operator.apps.matomo.MatomoInstallConfig
import eu.glasskube.operator.apps.matomo.MatomoReconciler
import eu.glasskube.operator.apps.matomo.configSecretName
import eu.glasskube.operator.apps.matomo.resourceLabels
import eu.glasskube.utils.decodeBase64
import eu.glasskube.utils.encodeBase64
import io.fabric8.kubernetes.api.model.Secret
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(
    labelSelector = MatomoReconciler.SELECTOR,
    resourceDiscriminator = MatomoConfigSecret.Discriminator::class
)
class MatomoConfigSecret(private val objectMapper: ObjectMapper) :
    CRUDKubernetesDependentResource<Secret, Matomo>(Secret::class.java) {

    class Discriminator :
        ResourceIDMatcherDiscriminator<Secret, Matomo>({ ResourceID(it.configSecretName, it.namespace) })

    override fun desired(primary: Matomo, context: Context<Matomo>) = secret {
        metadata {
            name(primary.configSecretName)
            namespace(primary.metadata.namespace)
            labels(primary.resourceLabels)
        }
        type = "Opaque"
        data = mapOf(MatomoDeployment.installJson to getInstallJson(primary, context).encodeBase64())
    }

    private fun getInstallJson(primary: Matomo, context: Context<Matomo>) = with(objectMapper) {
        readValue<MatomoInstallConfig>(MatomoConfigMap::class.java.getResource("config.json")!!)
            .apply {
                val smtp = primary.spec.smtp
                config.getValue("General").let { general ->
                    if (smtp == null) {
                        general["emails_enabled"] = 0
                    } else {
                        general["emails_enabled"] = 1
                        general["noreply_email_address"] = smtp.fromAddress
                    }
                }
                if (smtp != null) {
                    val authSecret = context.client.secrets()
                        .inNamespace(primary.metadata.namespace)
                        .withName(smtp.authSecret.name)
                        .require()
                    config["mail"] = mutableMapOf(
                        "transport" to "smtp",
                        "host" to smtp.host,
                        "port" to smtp.port,
                        "type" to "LOGIN",
                        "username" to authSecret.data.getValue("username").decodeBase64(),
                        "password" to authSecret.data.getValue("password").decodeBase64(),
                        "encryption" to if (smtp.tlsEnabled) "tls" else ""
                    )
                }
            }
            .let { writeValueAsString(it) }
    }
}
