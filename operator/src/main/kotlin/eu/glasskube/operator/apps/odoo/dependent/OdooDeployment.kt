package eu.glasskube.operator.apps.odoo.dependent

import eu.glasskube.kubernetes.api.model.apps.deployment
import eu.glasskube.kubernetes.api.model.apps.selector
import eu.glasskube.kubernetes.api.model.apps.spec
import eu.glasskube.kubernetes.api.model.apps.strategyRecreate
import eu.glasskube.kubernetes.api.model.apps.template
import eu.glasskube.kubernetes.api.model.configMap
import eu.glasskube.kubernetes.api.model.container
import eu.glasskube.kubernetes.api.model.containerPort
import eu.glasskube.kubernetes.api.model.env
import eu.glasskube.kubernetes.api.model.envVar
import eu.glasskube.kubernetes.api.model.httpGet
import eu.glasskube.kubernetes.api.model.intOrString
import eu.glasskube.kubernetes.api.model.livenessProbe
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.persistentVolumeClaim
import eu.glasskube.kubernetes.api.model.readinessProbe
import eu.glasskube.kubernetes.api.model.secretKeyRef
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.kubernetes.api.model.startupProbe
import eu.glasskube.kubernetes.api.model.volume
import eu.glasskube.kubernetes.api.model.volumeMount
import eu.glasskube.kubernetes.api.model.volumeMounts
import eu.glasskube.operator.apps.odoo.Odoo
import eu.glasskube.operator.apps.odoo.Odoo.Postgres.postgresHostName
import eu.glasskube.operator.apps.odoo.Odoo.Postgres.postgresSecretName
import eu.glasskube.operator.apps.odoo.OdooReconciler
import eu.glasskube.operator.apps.odoo.configMapName
import eu.glasskube.operator.apps.odoo.deploymentName
import eu.glasskube.operator.apps.odoo.identifyingLabel
import eu.glasskube.operator.apps.odoo.resourceLabels
import eu.glasskube.operator.apps.odoo.volumeName
import eu.glasskube.operator.config.ConfigService
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = OdooReconciler.SELECTOR)
class OdooDeployment(private val configService: ConfigService) :
    CRUDKubernetesDependentResource<Deployment, Odoo>(Deployment::class.java) {

    override fun desired(primary: Odoo, context: Context<Odoo>) = deployment {
        metadata {
            name(primary.deploymentName)
            namespace(primary.metadata.namespace)
            labels(primary.resourceLabels)
        }
        spec {
            selector {
                matchLabels = mapOf(primary.identifyingLabel)
            }
            strategyRecreate()
            template {
                metadata {
                    labels(primary.resourceLabels)
                    annotations(configService.getBackupAnnotations(Odoo.volumeName))
                }
                spec {
                    containers = listOf(
                        container {
                            name = Odoo.APP_NAME
                            image = "glasskube/${Odoo.APP_NAME}:${primary.spec.version}"
                            resources = primary.spec.resources
                            env {
                                envVar("HOST", primary.postgresHostName)
                                envVar("USER") { secretKeyRef(primary.postgresSecretName, "username") }
                                envVar("PASSWORD") { secretKeyRef(primary.postgresSecretName, "password") }
                            }
                            ports = listOf(containerPort { containerPort = 8069; name = "http" })
                            volumeMounts {
                                volumeMount {
                                    name = Odoo.volumeName
                                    mountPath = Odoo.volumePath
                                }
                                volumeMount {
                                    name = Odoo.configMapName
                                    mountPath = Odoo.configPath
                                    readOnly = true
                                }
                            }
                            command = mutableListOf("/glasskube/run.sh", "--proxy-mode")
                            if (!primary.spec.demoEnabled) {
                                command.addAll(listOf("--without-demo", "all"))
                            }
                            startupProbe {
                                periodSeconds = 10
                                successThreshold = 1
                                // If the container is still unresponsive after 10 minutes, it will be restarted
                                failureThreshold = 60
                                httpGet {
                                    path = PROBE_PATH
                                    port = intOrString("http")
                                }
                            }
                            livenessProbe {
                                periodSeconds = 10
                                successThreshold = 1
                                // If the container becomes unresponsive for 1 minute, it will be restarted
                                failureThreshold = 6
                                timeoutSeconds = 9
                                httpGet {
                                    path = PROBE_PATH
                                    port = intOrString("http")
                                }
                            }
                            readinessProbe {
                                periodSeconds = 10
                                successThreshold = 1
                                failureThreshold = 3
                                timeoutSeconds = 9
                                httpGet {
                                    path = PROBE_PATH
                                    port = intOrString("http")
                                }
                            }
                        }
                    )
                    initContainers = listOf(
                        container {
                            name = "odoo-init"
                            image = "debian:bullseye-slim"
                            volumeMounts {
                                volumeMount {
                                    name = Odoo.volumeName
                                    mountPath = Odoo.volumePath
                                }
                            }
                            command = listOf("chown", "-R", "101:101", Odoo.volumePath)
                        }
                    )
                    volumes = listOf(
                        volume(Odoo.volumeName) {
                            persistentVolumeClaim(primary.volumeName)
                        },
                        volume(Odoo.configMapName) {
                            configMap(primary.configMapName)
                        }
                    )
                }
            }
        }
    }

    private companion object {
        private const val PROBE_PATH = "/web/health"
    }
}
