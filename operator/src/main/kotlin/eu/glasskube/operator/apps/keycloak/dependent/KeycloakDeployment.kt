package eu.glasskube.operator.apps.keycloak.dependent

import eu.glasskube.kubernetes.api.model.apps.deployment
import eu.glasskube.kubernetes.api.model.apps.selector
import eu.glasskube.kubernetes.api.model.apps.spec
import eu.glasskube.kubernetes.api.model.apps.strategyRollingUpdate
import eu.glasskube.kubernetes.api.model.apps.template
import eu.glasskube.kubernetes.api.model.container
import eu.glasskube.kubernetes.api.model.containerPort
import eu.glasskube.kubernetes.api.model.env
import eu.glasskube.kubernetes.api.model.envVar
import eu.glasskube.kubernetes.api.model.envVars
import eu.glasskube.kubernetes.api.model.httpGet
import eu.glasskube.kubernetes.api.model.intOrString
import eu.glasskube.kubernetes.api.model.livenessProbe
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.kubernetes.api.model.readinessProbe
import eu.glasskube.kubernetes.api.model.secretKeyRef
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.operator.apps.keycloak.Keycloak
import eu.glasskube.operator.apps.keycloak.Keycloak.Postgres.postgresHostName
import eu.glasskube.operator.apps.keycloak.Keycloak.Postgres.postgresSecretName
import eu.glasskube.operator.apps.keycloak.KeycloakReconciler
import eu.glasskube.operator.apps.keycloak.appImage
import eu.glasskube.operator.apps.keycloak.discoveryServiceName
import eu.glasskube.operator.apps.keycloak.genericResourceName
import eu.glasskube.operator.apps.keycloak.resourceLabelSelector
import eu.glasskube.operator.apps.keycloak.resourceLabels
import io.fabric8.kubernetes.api.model.LocalObjectReference
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = KeycloakReconciler.SELECTOR)
class KeycloakDeployment : CRUDKubernetesDependentResource<Deployment, Keycloak>(Deployment::class.java) {
    override fun desired(primary: Keycloak, context: Context<Keycloak>) = deployment {
        metadata {
            name(primary.genericResourceName)
            namespace(primary.namespace)
            labels(primary.resourceLabels)
        }
        spec {
            selector {
                matchLabels = primary.resourceLabelSelector
            }
            strategyRollingUpdate()
            template {
                metadata {
                    labels(primary.resourceLabels)
                }
                spec {
                    containers = listOf(
                        container {
                            name = Keycloak.APP_NAME
                            image = primary.appImage
                            imagePullPolicy = "IfNotPresent"
                            ports = listOfNotNull(
                                containerPort {
                                    containerPort = 8080
                                    name = "http"
                                },
                                primary.spec.compatibility?.managementPortEnabled.takeIf { it == true }?.let {
                                    containerPort {
                                        containerPort = 9000
                                        name = "management"
                                    }
                                }
                            )
                            resources = primary.spec.resources
                            args = listOf("start")
                            env {
                                envVars(
                                    "KC_CACHE" to "ispn",
                                    "KC_CACHE_STACK" to "kubernetes",
                                    "KC_DB" to "postgres",
                                    "KC_DB_URL_HOST" to primary.postgresHostName,
                                    "KC_HEALTH_ENABLED" to "true",
                                    "KC_HOSTNAME_STRICT" to "false",
                                    "KC_HTTP_ENABLED" to "true",
                                    "KC_HTTP_PORT" to "8080",
                                    "KEYCLOAK_ADMIN" to "root",
                                    "KEYCLOAK_ADMIN_PASSWORD" to "glasskube-operator",
                                    "jgroups.dns.query" to primary.discoveryServiceName
                                )
                                if (primary.spec.compatibility?.hostnameV2Enabled == true) {
                                    envVars(
                                        "KC_HOSTNAME" to "https://" + primary.spec.host + "/",
                                        "KC_HOSTNAME_BACKCHANNEL_DYNAMIC" to "true",
                                    )
                                } else {
                                    envVars(
                                        "KC_HOSTNAME" to primary.spec.host,
                                        "KC_HOSTNAME_STRICT_BACKCHANNEL" to "false",
                                        "KC_PROXY" to "edge",
                                    )
                                }
                                envVar("KC_DB_USERNAME") { secretKeyRef(primary.postgresSecretName, "username") }
                                envVar("KC_DB_PASSWORD") { secretKeyRef(primary.postgresSecretName, "password") }
                            }
                            livenessProbe {
                                periodSeconds = 10
                                successThreshold = 1
                                failureThreshold = 6
                                timeoutSeconds = 9
                                httpGet {
                                    port = intOrString(
                                        if (primary.spec.compatibility?.managementPortEnabled == true) {
                                            "management"
                                        } else {
                                            "http"
                                        }
                                    )
                                    path = "/health/live"
                                }
                            }
                            readinessProbe {
                                periodSeconds = 10
                                successThreshold = 1
                                failureThreshold = 3
                                timeoutSeconds = 9
                                httpGet {
                                    port = intOrString(
                                        if (primary.spec.compatibility?.managementPortEnabled == true) {
                                            "management"
                                        } else {
                                            "http"
                                        }
                                    )
                                    path = "/health/ready"
                                }
                            }
                        }
                    )
                    imagePullSecrets = primary.spec.imagePullSecrets?.map { LocalObjectReference(it.name) }
                }
            }
        }
    }
}
