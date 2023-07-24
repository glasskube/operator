package eu.glasskube.operator.apps.glitchtip.dependent

import eu.glasskube.kubernetes.api.model.apps.deployment
import eu.glasskube.kubernetes.api.model.apps.selector
import eu.glasskube.kubernetes.api.model.apps.spec
import eu.glasskube.kubernetes.api.model.apps.strategyRecreate
import eu.glasskube.kubernetes.api.model.apps.strategyRollingUpdate
import eu.glasskube.kubernetes.api.model.apps.template
import eu.glasskube.kubernetes.api.model.capabilities
import eu.glasskube.kubernetes.api.model.configMapRef
import eu.glasskube.kubernetes.api.model.container
import eu.glasskube.kubernetes.api.model.containerPort
import eu.glasskube.kubernetes.api.model.emptyDir
import eu.glasskube.kubernetes.api.model.env
import eu.glasskube.kubernetes.api.model.envFrom
import eu.glasskube.kubernetes.api.model.envVar
import eu.glasskube.kubernetes.api.model.httpGet
import eu.glasskube.kubernetes.api.model.intOrString
import eu.glasskube.kubernetes.api.model.livenessProbe
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.kubernetes.api.model.persistentVolumeClaim
import eu.glasskube.kubernetes.api.model.readinessProbe
import eu.glasskube.kubernetes.api.model.secretKeyRef
import eu.glasskube.kubernetes.api.model.secretRef
import eu.glasskube.kubernetes.api.model.securityContext
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.kubernetes.api.model.startupProbe
import eu.glasskube.kubernetes.api.model.volume
import eu.glasskube.kubernetes.api.model.volumeMount
import eu.glasskube.kubernetes.api.model.volumeMounts
import eu.glasskube.operator.Affinities
import eu.glasskube.operator.apps.glitchtip.Glitchtip
import eu.glasskube.operator.apps.glitchtip.Glitchtip.Postgres.postgresSecretName
import eu.glasskube.operator.apps.glitchtip.GlitchtipReconciler
import eu.glasskube.operator.apps.glitchtip.configMapName
import eu.glasskube.operator.apps.glitchtip.genericResourceName
import eu.glasskube.operator.apps.glitchtip.resourceLabelSelector
import eu.glasskube.operator.apps.glitchtip.resourceLabels
import eu.glasskube.operator.apps.glitchtip.secretName
import eu.glasskube.operator.generic.condition.DeploymentReadyCondition
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(
    labelSelector = GlitchtipReconciler.SELECTOR,
    resourceDiscriminator = GlitchtipDeployment.Discriminator::class
)
class GlitchtipDeployment : CRUDKubernetesDependentResource<Deployment, Glitchtip>(Deployment::class.java) {

    internal class Discriminator : ResourceIDMatcherDiscriminator<Deployment, Glitchtip>({
        ResourceID(it.genericResourceName, it.namespace)
    })

    class ReadyPostCondition : DeploymentReadyCondition<Glitchtip>()

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
                                envVar("SERVER_ROLE", "web")
                                primary.spec.smtp?.also {
                                    envVar("SMTP_USERNAME") {
                                        secretKeyRef(it.authSecret.name, "username")
                                    }
                                    envVar("SMTP_PASSWORD") {
                                        secretKeyRef(it.authSecret.name, "password")
                                    }

                                    envVar("EMAIL_URL", "smtp://\$(SMTP_USERNAME):\$(SMTP_PASSWORD)@${it.host}:${it.port}")
                                    envVar("DEFAULT_FROM_EMAIL", it.fromAddress)
                                }
                                if (primary.spec.smtp == null) {
                                    envVar("EMAIL_URL", "consolemail://")
                                }
                            }
                            ports = listOf(
                                containerPort {
                                    containerPort = 8080
                                    name = HTTP
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
                                    port = HTTP.intOrString()
                                }
                            }
                            livenessProbe {
                                periodSeconds = 10
                                successThreshold = 1
                                // If the container becomes unresponsive for 1 minute, it will be restarted
                                failureThreshold = 6
                                httpGet {
                                    path = PROBE_PATH
                                    port = HTTP.intOrString()
                                }
                            }
                            readinessProbe {
                                periodSeconds = 10
                                successThreshold = 1
                                failureThreshold = 3
                                httpGet {
                                    path = PROBE_PATH
                                    port = HTTP.intOrString()
                                }
                            }
                            volumeMounts {
                                volumeMount {
                                    name = Glitchtip.UPLOADS_VOLUME_NAME
                                    mountPath = Glitchtip.UPLOADS_DIR
                                }
                                volumeMount {
                                    name = TMP_VOLUME
                                    mountPath = TMP_DIR
                                }
                            }
                            securityContext {
                                capabilities { drop = listOf("ALL") }
                                readOnlyRootFilesystem = true
                                allowPrivilegeEscalation = false
                            }
                        }
                    )
                    initContainers = mutableListOf(
                        container {
                            name = "${Glitchtip.APP_NAME}-migrate"
                            image = "${Glitchtip.APP_NAME}/${Glitchtip.APP_NAME}:v${Glitchtip.APP_VERSION}"
                            command = listOf("./manage.py")
                            args = listOf("migrate")
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
                            }
                        }
                    )
                    affinity = Affinities.podAffinityFor(primary.resourceLabelSelector)
                    securityContext {
                        fsGroup = Glitchtip.APP_UID
                        runAsGroup = Glitchtip.APP_UID
                        runAsUser = Glitchtip.APP_UID
                        runAsNonRoot = true
                    }
                    volumes = listOf(
                        volume(TMP_VOLUME) {
                            emptyDir()
                        },
                        volume(Glitchtip.UPLOADS_VOLUME_NAME) {
                            persistentVolumeClaim(primary.genericResourceName)
                        }
                    )
                }
            }
        }
    }

    companion object {
        private const val PROBE_PATH = "/_health/"
        private const val HTTP = "http"
        private const val TMP_VOLUME = "tmp"
        private const val TMP_DIR = "/tmp"
    }
}
