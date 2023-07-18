package eu.glasskube.operator.apps.glitchtip.dependent

import eu.glasskube.kubernetes.api.model.apps.deployment
import eu.glasskube.kubernetes.api.model.apps.selector
import eu.glasskube.kubernetes.api.model.apps.spec
import eu.glasskube.kubernetes.api.model.apps.strategyRecreate
import eu.glasskube.kubernetes.api.model.apps.strategyRollingUpdate
import eu.glasskube.kubernetes.api.model.apps.template
import eu.glasskube.kubernetes.api.model.configMapRef
import eu.glasskube.kubernetes.api.model.container
import eu.glasskube.kubernetes.api.model.containerPort
import eu.glasskube.kubernetes.api.model.env
import eu.glasskube.kubernetes.api.model.envFrom
import eu.glasskube.kubernetes.api.model.envVar
import eu.glasskube.kubernetes.api.model.httpGet
import eu.glasskube.kubernetes.api.model.intOrString
import eu.glasskube.kubernetes.api.model.livenessProbe
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.readinessProbe
import eu.glasskube.kubernetes.api.model.secretKeyRef
import eu.glasskube.kubernetes.api.model.secretRef
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.kubernetes.api.model.startupProbe
import eu.glasskube.operator.apps.glitchtip.Glitchtip
import eu.glasskube.operator.apps.glitchtip.Glitchtip.Postgres.postgresSecretName
import eu.glasskube.operator.apps.glitchtip.GlitchtipReconciler
import eu.glasskube.operator.apps.glitchtip.configMapName
import eu.glasskube.operator.apps.glitchtip.genericResourceName
import eu.glasskube.operator.apps.glitchtip.resourceLabelSelector
import eu.glasskube.operator.apps.glitchtip.resourceLabels
import eu.glasskube.operator.apps.glitchtip.secretName
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = GlitchtipReconciler.SELECTOR)
class GlitchtipDeployment : CRUDKubernetesDependentResource<Deployment, Glitchtip>(Deployment::class.java) {
    override fun desired(primary: Glitchtip, context: Context<Glitchtip>) = deployment {
        metadata {
            name = primary.genericResourceName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        spec {
            selector {
                matchLabels = primary.resourceLabelSelector
            }
            replicas = primary.spec.replicas
            if (primary.spec.replicas > 1) strategyRollingUpdate() else strategyRecreate()
            template {
                metadata {
                    labels = primary.resourceLabels
                }
                spec {
                    containers = listOf(
                        container {
                            name = Glitchtip.APP_NAME
                            image = "${Glitchtip.APP_NAME}/${Glitchtip.APP_NAME}:v${Glitchtip.APP_VERSION}"
                            envFrom {
                                configMapRef(primary.configMapName, false)
                                secretRef(primary.secretName, false)
                            }
                            env {
                                envVar("DATABASE_USER") {
                                    secretKeyRef(primary.postgresSecretName, "username")
                                }
                                envVar("DATABASE_PASSWORD") {
                                    secretKeyRef(primary.postgresSecretName, "password")
                                }
                                primary.spec.smtp?.also {
                                    envVar("SMTP_USERNAME") {
                                        secretKeyRef(it.authSecret.name, "username")
                                    }
                                    envVar("SMTP_PASSWORD") {
                                        secretKeyRef(it.authSecret.name, "password")
                                    }
                                }
                                // TODO reference EMAIL_URL with referenced vars
                                // https://glitchtip.com/documentation/install
                            }
                            ports = listOf(
                                containerPort {
                                    containerPort = 8080
                                    name = "http"
                                }
                            )
                            resources = primary.spec.resources
                            startupProbe {
                                periodSeconds = 10
                                successThreshold = 1
                                // If the container is still unresponsive after 10 minutes, it will be restarted
                                failureThreshold = 60
                                httpGet {
                                    path = PROBE_PATH
                                    port = "http".intOrString()
                                }
                            }
                            livenessProbe {
                                periodSeconds = 10
                                successThreshold = 1
                                // If the container becomes unresponsive for 1 minute, it will be restarted
                                failureThreshold = 6
                                httpGet {
                                    path = PROBE_PATH
                                    port = "http".intOrString()
                                }
                            }
                            readinessProbe {
                                periodSeconds = 10
                                successThreshold = 1
                                failureThreshold = 3
                                httpGet {
                                    path = PROBE_PATH
                                    port = "http".intOrString()
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    companion object {
        private const val PROBE_PATH = "/api/health"
    }
}
