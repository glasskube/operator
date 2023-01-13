package eu.glasskube.operator.odoo.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.secret
import eu.glasskube.operator.api.reconciler.NamedResourceDiscriminator
import eu.glasskube.operator.odoo.Odoo
import eu.glasskube.operator.odoo.OdooReconciler
import eu.glasskube.operator.odoo.dbSecretName
import eu.glasskube.operator.odoo.resourceLabels
import eu.glasskube.operator.secrets.SecretGenerator
import io.fabric8.kubernetes.api.model.Secret
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.ResourceUpdatePreProcessor

@KubernetesDependent(
    labelSelector = OdooReconciler.SELECTOR,
    resourceDiscriminator = OdooDatabaseSecret.Discriminator::class
)
class OdooDatabaseSecret :
    CRUDKubernetesDependentResource<Secret, Odoo>(Secret::class.java),
    ResourceUpdatePreProcessor<Secret> {

    class Discriminator : NamedResourceDiscriminator<Secret, Odoo>({ dbSecretName })

    override fun desired(primary: Odoo, context: Context<Odoo>) = secret {
        metadata {
            name = primary.dbSecretName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels + SecretGenerator.LABEL
            annotations = mapOf(SecretGenerator.generateKeys("password"))
        }
        type = "kubernetes.io/basic-auth"
        stringData = mapOf(
            "username" to Odoo.dbUsername
        )
    }

    override fun replaceSpecOnActual(actual: Secret, desired: Secret, context: Context<*>) = actual.apply {
        metadata.annotations.putAll(desired.metadata.annotations)
        metadata.labels.putAll(desired.metadata.labels)
        data.putAll(desired.data)
        stringData.putAll(desired.stringData)
    }
}
