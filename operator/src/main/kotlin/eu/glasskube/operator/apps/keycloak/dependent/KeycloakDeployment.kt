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
                            args = listOf("start")
                            env {
                                envVars(
                                    "KC_CACHE" to "ispn",
                                    "KC_CACHE_STACK" to "kubernetes",
                                    "KC_DB" to "postgres",
                                    "KC_DB_URL_HOST" to primary.postgresHostName,
                                    "KC_HEALTH_ENABLED" to "true",
                                    "KC_HOSTNAME" to primary.spec.host,
                                    "KC_HOSTNAME_STRICT" to "false",
                                    "KC_HOSTNAME_STRICT_BACKCHANNEL" to "false",
                                    "KC_HTTP_ENABLED" to "true",
                                    "KC_HTTP_PORT" to "8080",
                                    "KC_PROXY" to "passthrough",
                                    "KEYCLOAK_ADMIN" to "root",
                                    "KEYCLOAK_ADMIN_PASSWORD" to "glasskube-operator",
                                    "jgroups.dns.query" to primary.discoveryServiceName
                                )
                                envVar("KC_DB_USERNAME") { secretKeyRef(primary.postgresSecretName, "username") }
                                envVar("KC_DB_PASSWORD") { secretKeyRef(primary.postgresSecretName, "password") }
                            }
                            ports = listOf(
                                containerPort {
                                    containerPort = 8080
                                    name = "http"
                                }
                            )
                            livenessProbe {
                                periodSeconds = 10
                                successThreshold = 1
                                failureThreshold = 6
                                httpGet {
                                    port = intOrString("http")
                                    path = "/health/live"
                                }
                            }
                            readinessProbe {
                                periodSeconds = 10
                                successThreshold = 1
                                failureThreshold = 3
                                httpGet {
                                    port = intOrString("http")
                                    path = "/health/ready"
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
