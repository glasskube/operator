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
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.persistentVolumeClaim
import eu.glasskube.kubernetes.api.model.secretKeyRef
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.kubernetes.api.model.volume
import eu.glasskube.kubernetes.api.model.volumeMount
import eu.glasskube.kubernetes.api.model.volumeMounts
import eu.glasskube.operator.apps.gitea.Gitea
import eu.glasskube.operator.apps.odoo.Odoo
import eu.glasskube.operator.apps.odoo.OdooReconciler
import eu.glasskube.operator.apps.odoo.configMapName
import eu.glasskube.operator.apps.odoo.dbName
import eu.glasskube.operator.apps.odoo.dbSecretName
import eu.glasskube.operator.apps.odoo.deploymentName
import eu.glasskube.operator.apps.odoo.identifyingLabel
import eu.glasskube.operator.apps.odoo.resourceLabels
import eu.glasskube.operator.apps.odoo.volumeName
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = OdooReconciler.SELECTOR)
class OdooDeployment : CRUDKubernetesDependentResource<Deployment, Odoo>(Deployment::class.java) {
    private companion object {
        const val IMAGE = "gitea/gitea:${Gitea.APP_VERSION}"
    }
    override fun desired(primary: Odoo, context: Context<Odoo>) = deployment {
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
                    containers = listOf(
                        container {
                            name = Odoo.APP_NAME
                            image = Odoo.APP_IMAGE
                            resources = primary.spec.resources
                            env {
                                envVar("HOST", "${primary.dbName}-rw")
                                envVar("USER") { secretKeyRef(primary.dbSecretName, "username") }
                                envVar("PASSWORD") { secretKeyRef(primary.dbSecretName, "password") }
                            }
                            ports = listOf(containerPort { containerPort = 8069 })
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
}
