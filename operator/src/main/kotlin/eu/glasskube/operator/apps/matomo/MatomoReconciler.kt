package eu.glasskube.operator.apps.matomo

import eu.glasskube.kubernetes.client.patchOrUpdateStatus
import eu.glasskube.operator.api.reconciler.getSecondaryResource
import eu.glasskube.operator.api.reconciler.informerEventSource
import eu.glasskube.operator.apps.matomo.Matomo.Companion.APP_NAME
import eu.glasskube.operator.apps.matomo.dependent.MatomoConfigMap
import eu.glasskube.operator.apps.matomo.dependent.MatomoConfigSecret
import eu.glasskube.operator.apps.matomo.dependent.MatomoCronJob
import eu.glasskube.operator.apps.matomo.dependent.MatomoDatabaseSecret
import eu.glasskube.operator.apps.matomo.dependent.MatomoDeployment
import eu.glasskube.operator.apps.matomo.dependent.MatomoIngress
import eu.glasskube.operator.apps.matomo.dependent.MatomoMariaDB
import eu.glasskube.operator.apps.matomo.dependent.MatomoService
import eu.glasskube.operator.apps.matomo.dependent.MatomoVeleroBackupStorageLocation
import eu.glasskube.operator.apps.matomo.dependent.MatomoVeleroSchedule
import eu.glasskube.operator.apps.matomo.dependent.MatomoVeleroSecret
import eu.glasskube.operator.apps.matomo.dependent.MatomoVolume
import eu.glasskube.operator.generic.BaseReconciler
import eu.glasskube.operator.processing.CompositeSecondaryToPrimaryMapper
import eu.glasskube.operator.webhook.WebhookService
import eu.glasskube.utils.logger
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim
import io.fabric8.kubernetes.api.model.Secret
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.fabric8.kubernetes.client.KubernetesClient
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration
import io.javaoperatorsdk.operator.api.reconciler.EventSourceContext
import io.javaoperatorsdk.operator.api.reconciler.EventSourceInitializer
import io.javaoperatorsdk.operator.api.reconciler.dependent.Dependent
import io.javaoperatorsdk.operator.processing.event.source.informer.Mappers
import kotlin.jvm.optionals.getOrNull

@ControllerConfiguration(
    dependents = [
        Dependent(type = MatomoVolume::class, name = "MatomoVolume"),
        Dependent(
            type = MatomoDeployment::class,
            name = "MatomoDeployment",
            dependsOn = ["MatomoMariaDB", "MatomoVolume"]
        ),
        Dependent(
            type = MatomoDatabaseSecret::class,
            useEventSourceWithName = MatomoReconciler.SECRET_EVENT_SOURCE
        ),
        Dependent(
            type = MatomoConfigSecret::class,
            useEventSourceWithName = MatomoReconciler.SECRET_EVENT_SOURCE
        ),
        Dependent(type = MatomoConfigMap::class),
        Dependent(type = MatomoService::class),
        Dependent(type = MatomoIngress::class),
        Dependent(
            type = MatomoMariaDB::class,
            name = "MatomoMariaDB",
            readyPostcondition = MatomoMariaDB.ReadyPostCondition::class,
            activationCondition = MatomoMariaDB.ActivationCondition::class
        ),
        Dependent(type = MatomoCronJob::class, name = "MatomoCronJob", dependsOn = ["MatomoDeployment"]),
        Dependent(
            type = MatomoVeleroSecret::class,
            name = "MatomoVeleroSecret",
            reconcilePrecondition = MatomoVeleroSecret.ReconcilePrecondition::class,
            useEventSourceWithName = MatomoReconciler.SECRET_EVENT_SOURCE
        ),
        Dependent(
            type = MatomoVeleroBackupStorageLocation::class,
            name = "MatomoVeleroBackupStorageLocation",
            dependsOn = ["MatomoVeleroSecret"]
        ),
        Dependent(
            type = MatomoVeleroSchedule::class,
            name = "MatomoVeleroSchedule",
            dependsOn = ["MatomoVeleroBackupStorageLocation"]
        )
    ]
)
class MatomoReconciler(private val kubernetesClient: KubernetesClient, webhookService: WebhookService) :
    BaseReconciler<Matomo>(webhookService), EventSourceInitializer<Matomo> {

    override fun processReconciliation(resource: Matomo, context: Context<Matomo>) = with(context) {
        getSecondaryResources(PersistentVolumeClaim::class.java)
            .filter { it.metadata.name.endsWith("-misc") }
            .forEach {
                log.info("Deleting old persisted volume claim ${it.metadata.name}")
                kubernetesClient.persistentVolumeClaims().resource(it).delete()
            }

        resource.patchOrUpdateStatus(
            MatomoStatus(
                readyReplicas = getSecondaryResource<Deployment>().getOrNull()?.status?.readyReplicas ?: 0
            )
        )
    }

    override fun prepareEventSources(context: EventSourceContext<Matomo>) = with(context) {
        mapOf(
            SECRET_EVENT_SOURCE to informerEventSource<Secret> {
                withSecondaryToPrimaryMapper(
                    CompositeSecondaryToPrimaryMapper(
                        Mappers.fromOwnerReference(),
                        Mappers.fromDefaultAnnotations()
                    )
                )
            }
        )
    }

    companion object {
        const val LABEL = "glasskube.eu/Matomo"
        const val SELECTOR = "app.kubernetes.io/managed-by=glasskube-operator,app=$APP_NAME"
        internal const val SECRET_EVENT_SOURCE = "MatomoSecretEventSource"
        private val log = logger()
    }
}
