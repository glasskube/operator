package eu.glasskube.operator.apps.matomo.dependent

import eu.glasskube.kubernetes.api.model.apps.deployment
import eu.glasskube.kubernetes.api.model.apps.selector
import eu.glasskube.kubernetes.api.model.apps.spec
import eu.glasskube.kubernetes.api.model.apps.strategyRecreate
import eu.glasskube.kubernetes.api.model.apps.template
import eu.glasskube.kubernetes.api.model.configMap
import eu.glasskube.kubernetes.api.model.configMapRef
import eu.glasskube.kubernetes.api.model.container
import eu.glasskube.kubernetes.api.model.containerPort
import eu.glasskube.kubernetes.api.model.envFrom
import eu.glasskube.kubernetes.api.model.httpGet
import eu.glasskube.kubernetes.api.model.intOrString
import eu.glasskube.kubernetes.api.model.item
import eu.glasskube.kubernetes.api.model.items
import eu.glasskube.kubernetes.api.model.livenessProbe
import eu.glasskube.kubernetes.api.model.loggingId
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.persistentVolumeClaim
import eu.glasskube.kubernetes.api.model.readinessProbe
import eu.glasskube.kubernetes.api.model.secret
import eu.glasskube.kubernetes.api.model.secretRef
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.kubernetes.api.model.volume
import eu.glasskube.kubernetes.api.model.volumeMount
import eu.glasskube.kubernetes.api.model.volumeMounts
import eu.glasskube.operator.apps.matomo.Matomo
import eu.glasskube.operator.apps.matomo.MatomoReconciler
import eu.glasskube.operator.apps.matomo.appImage
import eu.glasskube.operator.apps.matomo.configMapName
import eu.glasskube.operator.apps.matomo.configSecretName
import eu.glasskube.operator.apps.matomo.databaseSecretName
import eu.glasskube.operator.apps.matomo.deploymentName
import eu.glasskube.operator.apps.matomo.resourceLabels
import eu.glasskube.operator.apps.matomo.volumeName
import eu.glasskube.operator.config.ConfigService
import eu.glasskube.utils.logger
import io.fabric8.kubernetes.api.model.DeletionPropagation
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import java.util.concurrent.TimeUnit

@KubernetesDependent(labelSelector = MatomoReconciler.SELECTOR)
class MatomoDeployment(private val configService: ConfigService) :
    CRUDKubernetesDependentResource<Deployment, Matomo>(Deployment::class.java) {

    companion object {
        private const val wwwDataVolumeName = "data"
        private const val configurationVolumeName = "matomo-configuration"
        private const val secretConfigVolumeName = "secret-configuration"
        const val htmlDir = "/var/www/html"
        private const val scriptsDir = "/glasskube/scripts"
        private const val configDir = "/glasskube/config"
        const val initSh = "init.sh"
        const val initShPath = "$scriptsDir/$initSh"
        const val installSh = "install.sh"
        const val installShPath = "$scriptsDir/$installSh"
        const val installJson = "install.json"
        const val installJsonPath = "$configDir/$installJson"
        private const val PROBE_PATH = "/matomo.php"
        private const val DELETION_TIMEOUT_MINUTES = 5L
        private val log = logger()
    }

    override fun handleUpdate(actual: Deployment, desired: Deployment, primary: Matomo, context: Context<Matomo>) =
        if (actual.spec.selector != desired.spec.selector) {
            log.info("Deployment selector of {} changed. will be recreated", primary.loggingId)
            recreate(actual, desired, primary, context)
        } else {
            super.handleUpdate(actual, desired, primary, context)
        }

    // The old pods must be gone before the new Deployment is created,
    // because two Matomo pods must not run their init containers on the same volume at the same time.
    private fun recreate(actual: Deployment, desired: Deployment, primary: Matomo, context: Context<Matomo>): Deployment {
        val resource = context.client.apps().deployments().resource(actual)
        resource.withPropagationPolicy(DeletionPropagation.FOREGROUND).delete()
        resource.waitUntilCondition({ it == null }, DELETION_TIMEOUT_MINUTES, TimeUnit.MINUTES)
        return handleCreate(desired, primary, context)
            .also { log.info("Deployment for {} recreated", primary.loggingId) }
    }

    override fun desired(primary: Matomo, context: Context<Matomo>) = deployment {
        metadata {
            name(primary.deploymentName)
            namespace(primary.metadata.namespace)
            labels(primary.resourceLabels)
        }
        spec {
            selector {
                matchLabels = primary.resourceLabels
            }
            strategyRecreate()
            template {
                metadata {
                    labels(primary.resourceLabels)
                    annotations(configService.getBackupAnnotations(primary, wwwDataVolumeName))
                }
                spec {
                    initContainers = listOf(
                        container {
                            name = "init"
                            image = primary.appImage
                            command = listOf("sh")
                            args = listOf(initShPath)
                            volumeMounts {
                                volumeMount {
                                    name = wwwDataVolumeName
                                    mountPath = htmlDir
                                }
                                volumeMount {
                                    name = configurationVolumeName
                                    mountPath = scriptsDir
                                    readOnly = true
                                }
                            }
                        },
                        container {
                            name = "install"
                            image = primary.appImage
                            command = listOf("sh")
                            args = listOf(installShPath)
                            envFrom {
                                secretRef(primary.databaseSecretName)
                                configMapRef(primary.configMapName)
                            }
                            volumeMounts {
                                volumeMount {
                                    name = wwwDataVolumeName
                                    mountPath = htmlDir
                                }
                                volumeMount {
                                    name = configurationVolumeName
                                    mountPath = scriptsDir
                                    readOnly = true
                                }
                                volumeMount {
                                    name = secretConfigVolumeName
                                    mountPath = configDir
                                    readOnly = true
                                }
                            }
                        },
                        container {
                            name = "chown"
                            image = primary.appImage
                            command = listOf("chown")
                            args = listOf("-R", "www-data:www-data", htmlDir)
                            volumeMounts {
                                volumeMount {
                                    name = wwwDataVolumeName
                                    mountPath = htmlDir
                                }
                            }
                        }
                    )
                    containers = listOf(
                        container {
                            name = Matomo.APP_NAME
                            image = primary.appImage
                            ports = listOf(
                                containerPort {
                                    containerPort = 80
                                    name = "http"
                                }
                            )
                            resources = primary.spec.resources
                            envFrom {
                                secretRef(primary.databaseSecretName)
                                configMapRef(primary.configMapName)
                            }
                            volumeMounts {
                                volumeMount {
                                    name = wwwDataVolumeName
                                    mountPath = htmlDir
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
                    volumes = listOf(
                        volume(wwwDataVolumeName) { persistentVolumeClaim(primary.volumeName) },
                        volume(secretConfigVolumeName) { secret(primary.configSecretName) },
                        volume(configurationVolumeName) {
                            configMap(primary.configMapName) {
                                defaultMode = 420
                                items {
                                    item(installSh, installSh)
                                    item(initSh, initSh)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
