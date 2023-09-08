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
import eu.glasskube.kubernetes.api.model.item
import eu.glasskube.kubernetes.api.model.items
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.persistentVolumeClaim
import eu.glasskube.kubernetes.api.model.secret
import eu.glasskube.kubernetes.api.model.secretRef
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.kubernetes.api.model.volume
import eu.glasskube.kubernetes.api.model.volumeMount
import eu.glasskube.kubernetes.api.model.volumeMounts
import eu.glasskube.operator.apps.matomo.Matomo
import eu.glasskube.operator.apps.matomo.MatomoReconciler
import eu.glasskube.operator.apps.matomo.configMapName
import eu.glasskube.operator.apps.matomo.configSecretName
import eu.glasskube.operator.apps.matomo.databaseSecretName
import eu.glasskube.operator.apps.matomo.deploymentName
import eu.glasskube.operator.apps.matomo.identifyingLabel
import eu.glasskube.operator.apps.matomo.resourceLabels
import eu.glasskube.operator.apps.matomo.volumeName
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = MatomoReconciler.SELECTOR)
class MatomoDeployment : CRUDKubernetesDependentResource<Deployment, Matomo>(Deployment::class.java) {

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
    }

    override fun desired(primary: Matomo, context: Context<Matomo>) = deployment {
        metadata {
            name = primary.deploymentName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        spec {
            selector {
                matchLabels = mapOf(primary.identifyingLabel)
            }
            strategyRecreate()
            template {
                metadata {
                    labels = primary.resourceLabels
                }
                spec {
                    initContainers = listOf(
                        container {
                            name = "init"
                            image = Matomo.APP_IMAGE
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
                            image = Matomo.APP_IMAGE
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
                            image = Matomo.APP_IMAGE
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
                            image = Matomo.APP_IMAGE
                            ports = listOf(containerPort { containerPort = 80 })
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
