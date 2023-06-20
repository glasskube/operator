package eu.glasskube.operator.apps.matomo.dependent

import eu.glasskube.kubernetes.api.model.apps.deployment
import eu.glasskube.kubernetes.api.model.apps.selector
import eu.glasskube.kubernetes.api.model.apps.spec
import eu.glasskube.kubernetes.api.model.apps.template
import eu.glasskube.kubernetes.api.model.configMap
import eu.glasskube.kubernetes.api.model.configMapRef
import eu.glasskube.kubernetes.api.model.container
import eu.glasskube.kubernetes.api.model.containerPort
import eu.glasskube.kubernetes.api.model.envFrom
import eu.glasskube.kubernetes.api.model.item
import eu.glasskube.kubernetes.api.model.items
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.secretRef
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.kubernetes.api.model.volume
import eu.glasskube.kubernetes.api.model.volumeMount
import eu.glasskube.kubernetes.api.model.volumeMounts
import eu.glasskube.operator.apps.matomo.Matomo
import eu.glasskube.operator.apps.matomo.MatomoReconciler
import eu.glasskube.operator.apps.matomo.configMapName
import eu.glasskube.operator.apps.matomo.deploymentName
import eu.glasskube.operator.apps.matomo.identifyingLabel
import eu.glasskube.operator.apps.matomo.resourceLabels
import eu.glasskube.operator.apps.matomo.secretName
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = MatomoReconciler.SELECTOR)
class MatomoDeployment : CRUDKubernetesDependentResource<Deployment, Matomo>(Deployment::class.java) {

    companion object {
        private const val matomoImage = "glasskube/matomo:4.14.2"
        private const val wwwDataVolumeName = "www-data"
        private const val configurationVolumeName = "matomo-configuration"
        private const val cronVolumeName = "cron"
        private const val htmlDir = "/var/www/html"
        private const val installDir = "/tmp/matomo"
        private const val cronDir = "/etc/cron.d"
        const val initSh = "init.sh"
        const val initShPath = "$installDir/$initSh"
        const val installSh = "install.sh"
        const val installShPath = "$installDir/$installSh"
        const val installJson = "install.json"
        const val installJsonPath = "$installDir/$installJson"
        const val archiveCron = "glasskube-matomo-archive-cron"
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
            template {
                metadata {
                    labels = primary.resourceLabels
                }
                spec {
                    initContainers = listOf(
                        container {
                            name = "init"
                            image = matomoImage
                            command = listOf("sh")
                            args = listOf(initShPath)
                            volumeMounts {
                                volumeMount {
                                    name = wwwDataVolumeName
                                    mountPath = htmlDir
                                }
                                volumeMount {
                                    name = configurationVolumeName
                                    mountPath = installDir
                                    readOnly = true
                                }
                            }
                        },
                        container {
                            name = "install"
                            image = matomoImage
                            command = listOf("sh")
                            args = listOf(installShPath)
                            envFrom {
                                secretRef(primary.secretName)
                                configMapRef(primary.configMapName)
                            }
                            volumeMounts {
                                volumeMount {
                                    name = wwwDataVolumeName
                                    mountPath = htmlDir
                                }
                                volumeMount {
                                    name = configurationVolumeName
                                    mountPath = installDir
                                    readOnly = true
                                }
                            }
                        },
                        container {
                            name = "chown"
                            image = matomoImage
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
                            name = "matomo"
                            image = matomoImage
                            ports = listOf(containerPort { containerPort = 80 })
                            envFrom {
                                secretRef(primary.secretName)
                                configMapRef(primary.configMapName)
                            }
                            volumeMounts {
                                volumeMount {
                                    name = wwwDataVolumeName
                                    mountPath = htmlDir
                                }
                                volumeMount {
                                    name = cronVolumeName
                                    mountPath = cronDir
                                }
                            }
                        }
                    )
                    volumes = listOf(
                        volume(wwwDataVolumeName),
                        volume(configurationVolumeName) {
                            configMap(primary.configMapName) {
                                defaultMode = 420
                                items {
                                    item(installJson, installJson)
                                    item(installSh, installSh)
                                    item(initSh, initSh)
                                }
                            }
                        },
                        volume(cronVolumeName) {
                            configMap(primary.configMapName) {
                                defaultMode = 420
                                items {
                                    item(archiveCron, archiveCron)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
