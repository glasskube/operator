package eu.glasskube.operator.apps.matomo.dependent

import com.fasterxml.jackson.module.kotlin.readValue
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.secret
import eu.glasskube.operator.apps.matomo.Matomo
import eu.glasskube.operator.apps.matomo.MatomoInstallConfig
import eu.glasskube.operator.apps.matomo.MatomoReconciler
import eu.glasskube.operator.apps.matomo.configSecretName
import eu.glasskube.operator.apps.matomo.resourceLabels
import eu.glasskube.operator.decodeBase64
import eu.glasskube.operator.encodeBase64
import io.fabric8.kubernetes.api.model.Secret
import io.fabric8.kubernetes.client.utils.Serialization
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(
    labelSelector = MatomoReconciler.SELECTOR,
    resourceDiscriminator = MatomoConfigSecret.Discriminator::class
)
class MatomoConfigSecret : CRUDKubernetesDependentResource<Secret, Matomo>(Secret::class.java) {
    class Discriminator : ResourceIDMatcherDiscriminator<Secret, Matomo>({ ResourceID(it.configSecretName) })

    override fun desired(primary: Matomo, context: Context<Matomo>) = secret {
        metadata {
            name = primary.configSecretName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        type = "Opaque"
        data = mapOf(MatomoDeployment.installJson to primary.installJson.encodeBase64())
    }

    private val Matomo.installJson
        get() = with(Serialization.jsonMapper()) {
            readValue<MatomoInstallConfig>(MatomoConfigMap::class.java.getResource("config.json")!!)
                .apply {
                    val smtp = spec.smtp
                    config.getValue("General").let { general ->
                        if (smtp == null) {
                            general["emails_enabled"] = 0
                        } else {
                            general["emails_enabled"] = 1
                            general["noreply_email_address"] = smtp.fromAddress
                        }
                    }
                    if (smtp != null) {
                        val authSecret = client.secrets()
                            .inNamespace(metadata.namespace)
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
