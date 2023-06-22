package eu.glasskube.operator.apps.metabase.dependent

import eu.glasskube.kubernetes.api.model.apps.deployment
import eu.glasskube.kubernetes.api.model.apps.selector
import eu.glasskube.kubernetes.api.model.apps.spec
import eu.glasskube.kubernetes.api.model.apps.strategy
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
import eu.glasskube.operator.apps.gitea.secretName
import eu.glasskube.operator.apps.metabase.Metabase
import eu.glasskube.operator.apps.metabase.MetabaseReconciler
import eu.glasskube.operator.apps.metabase.configMapName
import eu.glasskube.operator.apps.metabase.dbClusterName
import eu.glasskube.operator.apps.metabase.resourceLabelSelector
import eu.glasskube.operator.apps.metabase.resourceLabels
import eu.glasskube.operator.apps.metabase.secretName
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = MetabaseReconciler.SELECTOR)
class MetabaseDeployment : CRUDKubernetesDependentResource<Deployment, Metabase>(Deployment::class.java) {
    override fun desired(primary: Metabase, context: Context<Metabase>) = deployment {
        metadata {
            name = primary.metadata.name
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        spec {
            strategy("Recreate")
            selector {
                matchLabels = primary.resourceLabelSelector
            }
            template {
                metadata {
                    labels = primary.resourceLabels
                }
                spec {
                    containers = listOf(
                        container {
                            name = Metabase.APP_NAME
                            image = "${Metabase.APP_NAME}/${Metabase.APP_NAME}:v${Metabase.APP_VERSION}"
                            envFrom {
                                configMapRef(primary.configMapName, false)
                                secretRef(primary.secretName, false)
                            }
                            env {
                                envVar("MB_DB_USER") {
                                    secretKeyRef("${primary.dbClusterName}-app", "username")
                                }
                                envVar("MB_DB_PASS") {
                                    secretKeyRef("${primary.dbClusterName}-app", "password")
                                }
                                primary.spec.smtp?.also {
                                    envVar("MB_EMAIL_SMTP_USERNAME") {
                                        secretKeyRef(it.authSecret.name, "username")
                                    }
                                    envVar("MB_EMAIL_SMTP_PASSWORD") {
                                        secretKeyRef(it.authSecret.name, "password")
                                    }
                                }
                            }
                            ports = listOf(
                                containerPort {
                                    containerPort = 3000
                                    name = "http"
                                },
                                containerPort {
                                    containerPort = 9191
                                    name = "metabase-exp"
                                }
                            )
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
