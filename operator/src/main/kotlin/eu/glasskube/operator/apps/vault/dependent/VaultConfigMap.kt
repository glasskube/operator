package eu.glasskube.operator.apps.vault.dependent

import eu.glasskube.kubernetes.api.model.configMap
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.operator.apps.vault.Vault
import eu.glasskube.operator.apps.vault.VaultReconciler
import eu.glasskube.operator.apps.vault.genericResourceName
import eu.glasskube.operator.apps.vault.resourceLabels
import eu.glasskube.utils.resourceProperty
import io.fabric8.kubernetes.api.model.ConfigMap
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = VaultReconciler.SELECTOR)
class VaultConfigMap : CRUDKubernetesDependentResource<ConfigMap, Vault>(ConfigMap::class.java) {
    override fun desired(primary: Vault, context: Context<Vault>) = configMap {
        metadata {
            name(primary.genericResourceName)
            namespace(primary.namespace)
            labels(primary.resourceLabels)
        }
        data = mapOf(
            VaultStatefulSet.CONFIG_FILE_NAME to listOfNotNull(
                baseConfigHcl,
                primary.spec.serviceRegistration.takeIf { it.enabled }?.let { serviceRegistrationHcl },
                primary.spec.autoUnseal?.let { transitSealHcl },
                primary.spec.ui.takeIf { it.enabled }?.let { uiHcl }
            ).joinToString("\n")
        )
    }

    private val baseConfigHcl by resourceProperty()
    private val serviceRegistrationHcl by resourceProperty()
    private val transitSealHcl by resourceProperty()
    private val uiHcl by resourceProperty()
}
