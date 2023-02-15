package eu.glasskube.operator.matomo

import eu.glasskube.operator.Environment
import eu.glasskube.operator.matomo.dependent.MatomoConfigMap
import eu.glasskube.operator.matomo.dependent.MatomoDeployment
import eu.glasskube.operator.matomo.dependent.MatomoIngress
import eu.glasskube.operator.matomo.dependent.MatomoSecret
import eu.glasskube.operator.matomo.dependent.MatomoService
import eu.glasskube.operator.matomo.dependent.mariadb.MatomoDatabaseMariaDB
import eu.glasskube.operator.matomo.dependent.mariadb.MatomoGrantMariaDB
import eu.glasskube.operator.matomo.dependent.mariadb.MatomoMariaDB
import eu.glasskube.operator.matomo.dependent.mariadb.MatomoUserMariaDB
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration
import io.javaoperatorsdk.operator.api.reconciler.Reconciler
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl
import io.javaoperatorsdk.operator.api.reconciler.dependent.Dependent
import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.ClientRepresentation
import org.slf4j.LoggerFactory
import javax.ws.rs.ClientErrorException

@ControllerConfiguration(
    dependents = [
        Dependent(type = MatomoDeployment::class),
        Dependent(type = MatomoSecret::class),
        Dependent(type = MatomoConfigMap::class),
        Dependent(type = MatomoService::class),
        Dependent(type = MatomoIngress::class),
        Dependent(type = MatomoMariaDB::class),
        Dependent(type = MatomoDatabaseMariaDB::class),
        Dependent(type = MatomoUserMariaDB::class),
        Dependent(type = MatomoGrantMariaDB::class)
    ]
)
class MatomoReconciler(private val keycloak: Keycloak) : Reconciler<Matomo> {
    private val keycloakRealm by lazy { keycloak.realm(Environment.KEYCLOAK_REALM) }

    override fun reconcile(resource: Matomo, context: Context<Matomo>): UpdateControl<Matomo> {
        resource.createOrUpdateKeycloakClient()
        resource.status = MatomoStatus()
        return UpdateControl.patchStatus(resource)
    }

    private fun Matomo.createOrUpdateKeycloakClient() {
        when (val client = keycloakRealm.clients().findByClientId(genericResourceName).singleOrNull()) {
            null -> {
                log.info("client $genericResourceName does not exist. creating")
                createKeycloakClient()
            }
            else -> {
                log.info("client $genericResourceName exists. updating")
                updateKeycloakClient(client)
            }
        }
    }

    private fun Matomo.createKeycloakClient() {
        runCatching { keycloakRealm.clients().create(setupKeycloakClientRepresentation()) }
            .onSuccess { log.info("client $genericResourceName created") }
            .onFailure {
                if (it is ClientErrorException && it.response.status == 409) {
                    log.info("client $genericResourceName already exists")
                } else {
                    throw RuntimeException("could not create client $genericResourceName", it)
                }
            }
    }

    private fun Matomo.updateKeycloakClient(client: ClientRepresentation) {
        runCatching { keycloakRealm.clients().get(client.id).update(setupKeycloakClientRepresentation(client)) }
            .onSuccess { log.info("client $genericResourceName updated") }
            .onFailure { throw RuntimeException("could not create client $genericResourceName", it) }
    }

    private fun Matomo.setupKeycloakClientRepresentation(clientRepresentation: ClientRepresentation = ClientRepresentation()): ClientRepresentation =
        clientRepresentation.apply {
            protocol = "openid-connect"
            clientId = genericResourceName
            baseUrl = "https://${spec.host}/"
            redirectUris = listOf("https://${spec.host}/*")
            webOrigins = listOf("https://${spec.host}/")
            attributes = attributes.orEmpty() + ("post.logout.redirect.uris" to "https://${spec.host}/*")
            isPublicClient = false
            isDirectAccessGrantsEnabled = false
            isConsentRequired = true
            isFrontchannelLogout = true
        }

    companion object {
        const val LABEL = "glasskube.eu/Matomo"
        const val APP_NAME = "matomo"
        const val SELECTOR = "app.kubernetes.io/managed-by=glasskube-operator,app=$APP_NAME"

        private val log = LoggerFactory.getLogger(MatomoReconciler::class.java)
    }
}
