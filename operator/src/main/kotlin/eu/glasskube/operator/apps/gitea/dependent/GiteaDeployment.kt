package eu.glasskube.operator.apps.gitea.dependent

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
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.persistentVolumeClaim
import eu.glasskube.kubernetes.api.model.secretKeyRef
import eu.glasskube.kubernetes.api.model.secretRef
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.kubernetes.api.model.volume
import eu.glasskube.kubernetes.api.model.volumeMount
import eu.glasskube.kubernetes.api.model.volumeMounts
import eu.glasskube.operator.apps.gitea.Gitea
import eu.glasskube.operator.apps.gitea.Gitea.Postgres.postgresSecretName
import eu.glasskube.operator.apps.gitea.GiteaReconciler
import eu.glasskube.operator.apps.gitea.configMapName
import eu.glasskube.operator.apps.gitea.deploymentName
import eu.glasskube.operator.apps.gitea.genericResourceName
import eu.glasskube.operator.apps.gitea.iniConfigMapName
import eu.glasskube.operator.apps.gitea.resourceLabelSelector
import eu.glasskube.operator.apps.gitea.resourceLabels
import eu.glasskube.operator.apps.gitea.secretName
import eu.glasskube.operator.config.ConfigService
import eu.glasskube.utils.addTo
import io.fabric8.kubernetes.api.model.HTTPGetAction
import io.fabric8.kubernetes.api.model.IntOrString
import io.fabric8.kubernetes.api.model.Probe
import io.fabric8.kubernetes.api.model.SecurityContext
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(
    labelSelector = GiteaReconciler.SELECTOR,
    resourceDiscriminator = GiteaDeployment.Discriminator::class
)
class GiteaDeployment(private val configService: ConfigService) :
    CRUDKubernetesDependentResource<Deployment, Gitea>(Deployment::class.java) {
    internal class Discriminator : ResourceIDMatcherDiscriminator<Deployment, Gitea>({ ResourceID(it.deploymentName) })

    override fun desired(primary: Gitea, context: Context<Gitea>) = deployment {
        metadata {
            name = primary.deploymentName
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
                    annotations = configService.getBackupAnnotations(VOLUME_NAME)
                }
                spec {
                    containers = listOf(
                        container {
                            name = "gitea"
                            image = IMAGE
                            resources = primary.spec.resources
                            ports = listOf(
                                containerPort {
                                    name = "http"
                                    containerPort = 3000
                                },
                                containerPort {
                                    name = "ssh"
                                    containerPort = 22
                                }
                            )
                            envFrom {
                                configMapRef(primary.configMapName, false)
                            }
                            volumeMounts {
                                volumeMount {
                                    name = VOLUME_NAME
                                    mountPath = Gitea.WORK_DIR
                                }
                            }
                            livenessProbe = Probe().apply {
                                httpGet = HTTPGetAction().apply {
                                    path = "/api/healthz"
                                    port = IntOrString("http")
                                }
                                initialDelaySeconds = 200
                                timeoutSeconds = 5
                                periodSeconds = 10
                                successThreshold = 1
                                failureThreshold = 6
                            }
                        }
                    )
                    initContainers = mutableListOf(
                        container {
                            name = "chown-data"
                            image = IMAGE
                            command = listOf("chown")
                            args = listOf("git:git", Gitea.WORK_DIR)
                            volumeMounts {
                                volumeMount {
                                    name = VOLUME_NAME
                                    mountPath = Gitea.WORK_DIR
                                }
                            }
                        },
                        container {
                            name = "environment-to-ini"
                            image = IMAGE
                            command = listOf("/bin/sh")
                            args = listOf("-c", "mkdir -p /data/gitea/conf && environment-to-ini")
                            volumeMounts {
                                volumeMount {
                                    name = VOLUME_NAME
                                    mountPath = Gitea.WORK_DIR
                                }
                            }
                            envFrom {
                                configMapRef(primary.configMapName, false)
                                configMapRef(primary.iniConfigMapName, false)
                                secretRef(primary.secretName, false)
                            }
                            env {
                                envVar("GITEA__database__USER") {
                                    secretKeyRef(primary.postgresSecretName, "username")
                                }
                                envVar("GITEA__database__PASSWD") {
                                    secretKeyRef(primary.postgresSecretName, "password")
                                }
                            }
                            securityContext = SecurityContext().apply {
                                runAsUser = 1000
                                runAsGroup = 1000
                            }
                        },
                        container {
                            name = "gitea-migrate"
                            image = IMAGE
                            command = listOf("gitea")
                            args = listOf("migrate")
                            volumeMounts {
                                volumeMount {
                                    name = VOLUME_NAME
                                    mountPath = Gitea.WORK_DIR
                                }
                            }
                            envFrom {
                                configMapRef(primary.configMapName, false)
                            }
                            securityContext = SecurityContext().apply {
                                runAsUser = 1000
                                runAsGroup = 1000
                            }
                        }
                    ).apply {
                        primary.spec.adminSecret
                            ?.let { adminSecret ->
                                container {
                                    name = "gitea-admin-user"
                                    image = IMAGE
                                    command = listOf("/bin/sh")
                                    args = listOf(
                                        "-c",
                                        """
                                            gitea admin user create --admin \
                                                --username ${'$'}GITEA_ADMIN_USER \
                                                --password ${'$'}GITEA_ADMIN_PASSWORD \
                                                --email ${'$'}GITEA_ADMIN_EMAIL \
                                                --must-change-password=false \
                                              || gitea admin user change-password \
                                                --username ${'$'}GITEA_ADMIN_USER \
                                                --password ${'$'}GITEA_ADMIN_PASSWORD
                                        """.trimIndent()
                                    )
                                    volumeMounts {
                                        volumeMount {
                                            name = VOLUME_NAME
                                            mountPath = Gitea.WORK_DIR
                                        }
                                    }
                                    envFrom {
                                        configMapRef(primary.configMapName, false)
                                        secretRef(adminSecret.name, false)
                                    }
                                    securityContext = SecurityContext().apply {
                                        runAsUser = 1000
                                        runAsGroup = 1000
                                    }
                                }
                            }
                            ?.addTo(this)
                    }
                    volumes = listOf(
                        volume(VOLUME_NAME) {
                            persistentVolumeClaim(primary.genericResourceName)
                        }
                    )
                }
            }
        }
    }

    private companion object {
        const val IMAGE = "gitea/gitea:${Gitea.APP_VERSION}"
        private const val VOLUME_NAME = "data"
    }
}
