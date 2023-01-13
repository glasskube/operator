package eu.glasskube.operator.odoo

import eu.glasskube.operator.api.reconciler.informerEventSource
import eu.glasskube.operator.odoo.dependent.OdooConfigMap
import eu.glasskube.operator.odoo.dependent.OdooDatabaseSecret
import eu.glasskube.operator.odoo.dependent.OdooDatabaseSuperuserSecret
import eu.glasskube.operator.odoo.dependent.OdooDeployment
import eu.glasskube.operator.odoo.dependent.OdooIngress
import eu.glasskube.operator.odoo.dependent.OdooPersistentVolumeClaim
import eu.glasskube.operator.odoo.dependent.OdooPostgresCluster
import eu.glasskube.operator.odoo.dependent.OdooService
import io.fabric8.kubernetes.api.model.Secret
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration
import io.javaoperatorsdk.operator.api.reconciler.EventSourceContext
import io.javaoperatorsdk.operator.api.reconciler.EventSourceInitializer
import io.javaoperatorsdk.operator.api.reconciler.Reconciler
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl
import io.javaoperatorsdk.operator.api.reconciler.dependent.Dependent

@ControllerConfiguration(
    dependents = [
        Dependent(type = OdooDeployment::class),
        Dependent(type = OdooConfigMap::class),
        Dependent(type = OdooPersistentVolumeClaim::class),
        Dependent(type = OdooService::class),
        Dependent(type = OdooIngress::class),
        Dependent(type = OdooPostgresCluster::class),
        Dependent(
            type = OdooDatabaseSecret::class,
            useEventSourceWithName = OdooReconciler.SECRETS_EVENT_SOURCE_NAME
        ),
        Dependent(
            type = OdooDatabaseSuperuserSecret::class,
            useEventSourceWithName = OdooReconciler.SECRETS_EVENT_SOURCE_NAME
        )
    ]
)
class OdooReconciler : Reconciler<Odoo>, EventSourceInitializer<Odoo> {
    override fun reconcile(resource: Odoo, context: Context<Odoo>): UpdateControl<Odoo> =
        when (resource.status) {
            null -> UpdateControl.patchStatus(resource.apply { status = OdooStatus() })
            else -> UpdateControl.noUpdate()
        }

    override fun prepareEventSources(context: EventSourceContext<Odoo>) = with(context) {
        mutableMapOf(SECRETS_EVENT_SOURCE_NAME to informerEventSource<Secret>())
    }

    companion object {
        const val LABEL = "glasskube.eu/Odoo"
        const val APP_NAME = "odoo"
        const val SELECTOR = "app.kubernetes.io/managed-by=glasskube-operator,app=$APP_NAME"
        const val SECRETS_EVENT_SOURCE_NAME = "OdooSecretEventSource"
    }
}
