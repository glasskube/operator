package eu.glasskube.operator.odoo.dependent

import eu.glasskube.kubernetes.api.model.apps.RECREATE
import eu.glasskube.kubernetes.api.model.apps.deployment
import eu.glasskube.kubernetes.api.model.apps.selector
import eu.glasskube.kubernetes.api.model.apps.spec
import eu.glasskube.kubernetes.api.model.apps.strategy
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
import eu.glasskube.operator.decodeBase64
import eu.glasskube.operator.odoo.Odoo
import eu.glasskube.operator.odoo.OdooReconciler
import eu.glasskube.operator.odoo.configMapName
import eu.glasskube.operator.odoo.dbName
import eu.glasskube.operator.odoo.dbSecretName
import eu.glasskube.operator.odoo.deploymentName
import eu.glasskube.operator.odoo.identifyingLabel
import eu.glasskube.operator.odoo.resourceLabels
import eu.glasskube.operator.odoo.volumeName
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = OdooReconciler.SELECTOR)
class OdooDeployment : CRUDKubernetesDependentResource<Deployment, Odoo>(Deployment::class.java) {
    override fun desired(primary: Odoo, context: Context<Odoo>) = deployment {
        metadata {
            name = primary.deploymentName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        spec {
            strategy(RECREATE)
            selector {
                matchLabels = mapOf(primary.identifyingLabel)
            }
            template {
                metadata {
                    labels = primary.resourceLabels
                }
                spec {
                    containers = listOf(
                        container {
                            name = "odoo"
                            image = "glasskube/odoo:16.0.20230317"
                            imagePullPolicy = "IfNotPresent"
                            env {
                                envVar("HOST", "${primary.dbName}-rw")
                                envVar("USER") { secretKeyRef(primary.dbSecretName, "username", false) }
                                envVar("PASSWORD") { secretKeyRef(primary.dbSecretName, "password", false) }
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
                            command = listOf("/glasskube/run.sh")
                            args = listOf("--proxy-mode") + primary.demoArgs.orEmpty() + primary.smtpArgs.orEmpty()
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

    private val Odoo.demoArgs: List<String>?
        get() = spec.demoEnabled.takeIf { !it }?.let { listOf("--without-demo", "all") }

    private val Odoo.smtpArgs: List<String>?
        get() = spec.smtp?.run {
            val authSecret = kubernetesClient.secrets()
                .inNamespace(metadata.namespace)
                .withName(authSecret.name)
                .require()
            listOf(
                "--smtp", host,
                "--smtp-port", port.toString(),
                "--smtp-user", authSecret.data.getValue("username").decodeBase64(),
                "--smtp-password", authSecret.data.getValue("password").decodeBase64(),
                "--email-from", fromAddress
            ) +
                ssl.takeIf { it }?.let { listOf("--smtp-ssl") }.orEmpty() +
                fromFilter?.let { listOf("--from-filter", it) }.orEmpty()
        }
}
