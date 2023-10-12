package eu.glasskube.operator.apps.keycloak

import eu.glasskube.kubernetes.client.patchOrUpdateStatus
import eu.glasskube.operator.Labels
import eu.glasskube.operator.api.reconciler.getSecondaryResource
import eu.glasskube.operator.api.reconciler.informerEventSource
import eu.glasskube.operator.apps.keycloak.dependent.KeycloakDeployment
import eu.glasskube.operator.apps.keycloak.dependent.KeycloakDiscoveryService
import eu.glasskube.operator.apps.keycloak.dependent.KeycloakIngress
import eu.glasskube.operator.apps.keycloak.dependent.KeycloakPostgresBackup
import eu.glasskube.operator.apps.keycloak.dependent.KeycloakPostgresBackupBucket
import eu.glasskube.operator.apps.keycloak.dependent.KeycloakPostgresCluster
import eu.glasskube.operator.apps.keycloak.dependent.KeycloakService
import eu.glasskube.operator.generic.BaseReconciler
import eu.glasskube.operator.infra.postgres.PostgresCluster
import eu.glasskube.operator.infra.postgres.isReady
import eu.glasskube.operator.webhook.WebhookService
import io.fabric8.kubernetes.api.model.Service
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration
import io.javaoperatorsdk.operator.api.reconciler.EventSourceContext
import io.javaoperatorsdk.operator.api.reconciler.EventSourceInitializer
import io.javaoperatorsdk.operator.api.reconciler.dependent.Dependent
import kotlin.jvm.optionals.getOrDefault

@ControllerConfiguration(
    dependents = [
        Dependent(
            type = KeycloakPostgresBackupBucket::class,
            name = "KeycloakPostgresBackupBucket",
            reconcilePrecondition = KeycloakPostgresBackupBucket.ReconcilePrecondition::class
        ),
        Dependent(
            type = KeycloakPostgresCluster::class,
            name = "KeycloakPostgresCluster",
            readyPostcondition = KeycloakPostgresCluster.ReadyCondition::class
        ),
        Dependent(
            type = KeycloakPostgresBackup::class,
            name = "KeycloakPostgresBackup",
            dependsOn = ["KeycloakPostgresCluster"],
            reconcilePrecondition = KeycloakPostgresBackup.ReconcilePrecondition::class
        ),
        Dependent(
            type = KeycloakDeployment::class,
            name = "KeycloakDeployment",
            dependsOn = ["KeycloakPostgresCluster"]
        ),
        Dependent(
            type = KeycloakService::class,
            name = "KeycloakService",
            useEventSourceWithName = KeycloakReconciler.SERVICE_EVENT_SOURCE
        ),
        Dependent(
            type = KeycloakDiscoveryService::class,
            name = "KeycloakDiscoveryService",
            useEventSourceWithName = KeycloakReconciler.SERVICE_EVENT_SOURCE
        ),
        Dependent(type = KeycloakIngress::class, name = "KeycloakIngress")
    ]
)
class KeycloakReconciler(webhookService: WebhookService) :
    BaseReconciler<Keycloak>(webhookService), EventSourceInitializer<Keycloak> {

    override fun processReconciliation(resource: Keycloak, context: Context<Keycloak>) = with(context) {
        resource.patchOrUpdateStatus(
            KeycloakStatus(
                getSecondaryResource<Deployment>().map { it.status?.readyReplicas ?: 0 }.getOrDefault(0),
                getSecondaryResource<PostgresCluster>().map { it.isReady }.getOrDefault(false)
            )
        )
    }

    override fun prepareEventSources(context: EventSourceContext<Keycloak>) = with(context) {
        mutableMapOf(SERVICE_EVENT_SOURCE to informerEventSource<Service>())
    }

    companion object {
        const val SELECTOR =
            "${Labels.MANAGED_BY_GLASSKUBE},${Labels.PART_OF}=${Keycloak.APP_NAME},${Labels.NAME}=${Keycloak.APP_NAME}"
        internal const val SERVICE_EVENT_SOURCE = "KeycloakServiceEventSource"
    }
}
