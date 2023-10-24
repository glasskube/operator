package eu.glasskube.operator.apps.gitlab.dependent

import eu.glasskube.kubernetes.api.model.apps.deployment
import eu.glasskube.kubernetes.api.model.apps.selector
import eu.glasskube.kubernetes.api.model.apps.spec
import eu.glasskube.kubernetes.api.model.apps.strategyRecreate
import eu.glasskube.kubernetes.api.model.apps.template
import eu.glasskube.kubernetes.api.model.configMapRef
import eu.glasskube.kubernetes.api.model.container
import eu.glasskube.kubernetes.api.model.containerPort
import eu.glasskube.kubernetes.api.model.createEnv
import eu.glasskube.kubernetes.api.model.envFrom
import eu.glasskube.kubernetes.api.model.envVar
import eu.glasskube.kubernetes.api.model.httpGet
import eu.glasskube.kubernetes.api.model.intOrString
import eu.glasskube.kubernetes.api.model.livenessProbe
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.persistentVolumeClaim
import eu.glasskube.kubernetes.api.model.readinessProbe
import eu.glasskube.kubernetes.api.model.secret
import eu.glasskube.kubernetes.api.model.secretKeyRef
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.kubernetes.api.model.startupProbe
import eu.glasskube.kubernetes.api.model.volume
import eu.glasskube.kubernetes.api.model.volumeMount
import eu.glasskube.kubernetes.api.model.volumeMounts
import eu.glasskube.operator.apps.gitlab.Gitlab
import eu.glasskube.operator.apps.gitlab.Gitlab.Postgres.postgresSecretName
import eu.glasskube.operator.apps.gitlab.GitlabReconciler
import eu.glasskube.operator.apps.gitlab.GitlabRegistryStorageSpec
import eu.glasskube.operator.apps.gitlab.GitlabSmtp
import eu.glasskube.operator.apps.gitlab.configMapName
import eu.glasskube.operator.apps.gitlab.genericRegistryResourceName
import eu.glasskube.operator.apps.gitlab.genericResourceName
import eu.glasskube.operator.apps.gitlab.resourceLabelSelector
import eu.glasskube.operator.apps.gitlab.resourceLabels
import eu.glasskube.operator.apps.gitlab.volumeName
import eu.glasskube.operator.config.ConfigService
import io.fabric8.kubernetes.api.model.VolumeMount
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = GitlabReconciler.SELECTOR)
class GitlabDeployment(private val configService: ConfigService) :
    CRUDKubernetesDependentResource<Deployment, Gitlab>(Deployment::class.java) {

    override fun desired(primary: Gitlab, context: Context<Gitlab>) = deployment {
        metadata {
            name(primary.genericResourceName)
            namespace(primary.metadata.namespace)
            labels(primary.resourceLabels)
        }
        spec {
            selector {
                matchLabels = primary.resourceLabelSelector
            }
            strategyRecreate()
            template {
                metadata {
                    labels(primary.resourceLabels)
                    annotations(configService.getBackupAnnotations(VOLUME_NAME))
                }
                spec {
                    volumes = listOf(
                        volume(VOLUME_NAME) { persistentVolumeClaim(primary.volumeName) }
                    ) + primary.spec.registry.let {
                        volume(TLS_VOLUME_NAME) { secret(primary.genericRegistryResourceName) }
                    }
                    containers = listOf(
                        container {
                            name = Gitlab.APP_NAME
                            image = "${Gitlab.APP_IMAGE}:${primary.spec.version}-ce.0"
                            resources = primary.spec.resources
                            envFrom {
                                configMapRef(primary.configMapName)
                            }
                            env = primary.smtpEnv + primary.databaseEnv + primary.rootPasswordEnv + primary.registryEnv
                            volumeMounts {
                                volumeMount {
                                    mountPath = "/var/opt/gitlab"
                                    name = VOLUME_NAME
                                    subPath = "data"
                                }
                                volumeMount {
                                    mountPath = "/etc/gitlab"
                                    name = VOLUME_NAME
                                    subPath = "config"
                                }
                                volumeMount {
                                    mountPath = "/var/log/gitlab"
                                    name = VOLUME_NAME
                                    subPath = "logs"
                                }
                                primary.spec.registry?.let { tlsVolumeMount() }
                            }
                            ports = listOf(
                                containerPort {
                                    containerPort = 22
                                    name = "ssh"
                                },
                                containerPort {
                                    containerPort = 80
                                    name = "http"
                                },
                                containerPort {
                                    containerPort = 8082
                                    name = "sidekiq-exp"
                                },
                                containerPort {
                                    containerPort = 9168
                                    name = "gitlab-exp"
                                },
                                containerPort {
                                    containerPort = 9236
                                    name = "gitaly-exp"
                                }
                            ) + primary.spec.registry.let {
                                containerPort {
                                    containerPort = 5000
                                    name = "registry"
                                }
                                containerPort {
                                    containerPort = 5443
                                    name = "registry-ssl"
                                }
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
                                httpGet {
                                    path = PROBE_PATH
                                    port = intOrString("http")
                                }
                            }
                            readinessProbe {
                                periodSeconds = 10
                                successThreshold = 1
                                failureThreshold = 3
                                httpGet {
                                    path = PROBE_PATH
                                    port = intOrString("http")
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    private fun MutableList<VolumeMount>.tlsVolumeMount() {
        volumeMount {
            name = TLS_VOLUME_NAME
            mountPath = TLS_VOLUME_DIR
            readOnly = true
        }
    }

    private val Gitlab.smtpEnv get() = spec.smtp?.smtpEnv.orEmpty()

    private val GitlabSmtp.smtpEnv
        get() = createEnv {
            envVar("SMTP_USERNAME") {
                secretKeyRef(authSecret.name, "username")
            }
            envVar("SMTP_PASSWORD") {
                secretKeyRef(authSecret.name, "password")
            }
        }
    private val Gitlab.databaseEnv
        get() = createEnv {
            envVar("DB_PASSWORD") {
                secretKeyRef(postgresSecretName, "password")
            }
        }
    private val Gitlab.rootPasswordEnv
        get() = createEnv {
            when (val selector = spec.initialRootPasswordSecret) {
                null ->
                    envVar("INITIAL_ROOT_PASSWORD", "glasskube-operator")

                else ->
                    envVar("INITIAL_ROOT_PASSWORD") {
                        secretKeyRef(selector.name, selector.key)
                    }
            }
        }

    private val Gitlab.registryEnv
        get() = createEnv {
            when (val registry = spec.registry) {
                null ->
                    envVar("REGISTRY_ENABLED", false.toString())

                else -> {
                    envVar("REGISTRY_ENABLED", true.toString())
                    envVar("GITLAB_REGISTRY_HOST", registry.host)
                }
            }
        } + spec.registry?.storage?.registryStorageEnv.orEmpty()

    private val GitlabRegistryStorageSpec.registryStorageEnv
        get() = createEnv {
            envVar("REGISTRY_OBJECTSTORE_ENABLED", true.toString())
            envVar("REGISTRY_OBJECTSTORE_S3_BUCKET", s3.bucket)
            envVar("REGISTRY_OBJECTSTORE_S3_REGION", s3.region)
            envVar("REGISTRY_OBJECTSTORE_S3_HOST", s3.hostname)
            envVar("REGISTRY_OBJECTSTORE_S3_USEPATH_STYLE", s3.usePathStyle.toString())
            envVar("REGISTRY_OBJECTSTORE_S3_KEY") {
                secretKeyRef(s3.accessKeySecret.name, s3.accessKeySecret.key)
            }
            envVar("REGISTRY_OBJECTSTORE_S3_SECRET") {
                secretKeyRef(s3.secretKeySecret.name, s3.secretKeySecret.key)
            }
        }

    companion object {
        private const val VOLUME_NAME = "data"
        private const val TLS_VOLUME_NAME = "tls"
        private const val TLS_VOLUME_DIR = "/etc/gitlab/ssl/"
        private const val PROBE_PATH = "/users/sign_in"
    }
}
