package eu.glasskube.operator.apps.vault

import eu.glasskube.kubernetes.client.patchOrUpdateStatus
import eu.glasskube.operator.Labels
import eu.glasskube.operator.api.reconciler.getSecondaryResource
import eu.glasskube.operator.api.reconciler.informerEventSource
import eu.glasskube.operator.apps.vault.dependent.VaultConfigMap
import eu.glasskube.operator.apps.vault.dependent.VaultIngress
import eu.glasskube.operator.apps.vault.dependent.VaultMinioBucket
import eu.glasskube.operator.apps.vault.dependent.VaultPostgresBackup
import eu.glasskube.operator.apps.vault.dependent.VaultPostgresCluster
import eu.glasskube.operator.apps.vault.dependent.VaultRole
import eu.glasskube.operator.apps.vault.dependent.VaultRoleBinding
import eu.glasskube.operator.apps.vault.dependent.VaultService
import eu.glasskube.operator.apps.vault.dependent.VaultServiceAccount
import eu.glasskube.operator.apps.vault.dependent.VaultServiceHeadless
import eu.glasskube.operator.apps.vault.dependent.VaultStatefulSet
import eu.glasskube.operator.infra.postgres.PostgresCluster
import eu.glasskube.operator.infra.postgres.isReady
import io.fabric8.kubernetes.api.model.Service
import io.fabric8.kubernetes.api.model.apps.StatefulSet
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration
import io.javaoperatorsdk.operator.api.reconciler.EventSourceContext
import io.javaoperatorsdk.operator.api.reconciler.EventSourceInitializer
import io.javaoperatorsdk.operator.api.reconciler.Reconciler
import io.javaoperatorsdk.operator.api.reconciler.dependent.Dependent
import kotlin.jvm.optionals.getOrDefault

@ControllerConfiguration(
    dependents = [
        Dependent(type = VaultConfigMap::class, name = "VaultConfigMap"),
        Dependent(type = VaultMinioBucket::class, name = "VaultMinioBucket"),
        Dependent(type = VaultIngress::class, name = "VaultIngress"),
        Dependent(
            type = VaultPostgresCluster::class,
            name = "VaultPostgresCluster",
            readyPostcondition = VaultPostgresCluster.ReadyCondition::class,
            dependsOn = ["VaultMinioBucket"]
        ),
        Dependent(
            type = VaultService::class,
            name = "VaultService",
            useEventSourceWithName = VaultReconciler.SERVICE_EVENT_SOURCE
        ),
        Dependent(
            type = VaultServiceHeadless::class,
            name = "VaultServiceHeadless",
            useEventSourceWithName = VaultReconciler.SERVICE_EVENT_SOURCE
        ),
        Dependent(type = VaultServiceAccount::class, name = "VaultServiceAccount"),
        Dependent(
            type = VaultRole::class,
            name = "VaultRole",
            reconcilePrecondition = VaultRole.ReconcilePrecondition::class
        ),
        Dependent(
            type = VaultRoleBinding::class,
            name = "VaultRoleBinding",
            reconcilePrecondition = VaultRoleBinding.ReconcilePrecondition::class,
            dependsOn = ["VaultRole", "VaultServiceAccount"]
        ),
        Dependent(
            type = VaultStatefulSet::class,
            name = "VaultStatefulSet",
            dependsOn = ["VaultPostgresCluster", "VaultServiceAccount", "VaultServiceHeadless", "VaultConfigMap"]
        ),
        Dependent(type = VaultPostgresBackup::class, name = "VaultPostgresBackup", dependsOn = ["VaultPostgresCluster"])
    ]
)
class VaultReconciler : Reconciler<Vault>, EventSourceInitializer<Vault> {
    override fun reconcile(resource: Vault, context: Context<Vault>) = with(context) {
        resource.patchOrUpdateStatus(
            VaultStatus(
                getSecondaryResource<StatefulSet>().map { it.status?.readyReplicas ?: 0 }.getOrDefault(0),
                getSecondaryResource<PostgresCluster>().map { it.isReady }.getOrDefault(false)
            )
        )
    }

    override fun prepareEventSources(context: EventSourceContext<Vault>) = with(context) {
        mutableMapOf(SERVICE_EVENT_SOURCE to informerEventSource<Service>())
    }

    companion object {
        internal const val SELECTOR =
            "${Labels.MANAGED_BY_GLASSKUBE},${Labels.PART_OF}=${Vault.APP_NAME},${Labels.NAME}=${Vault.APP_NAME}"
        internal const val SERVICE_EVENT_SOURCE = "VaultServiceEventSource"
    }
}
