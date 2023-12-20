package eu.glasskube.operator.apps.nextcloud.dependent

import eu.glasskube.kubernetes.api.model.apps.deployment
import eu.glasskube.kubernetes.api.model.apps.selector
import eu.glasskube.kubernetes.api.model.apps.spec
import eu.glasskube.kubernetes.api.model.apps.strategyRecreate
import eu.glasskube.kubernetes.api.model.apps.template
import eu.glasskube.kubernetes.api.model.configMap
import eu.glasskube.kubernetes.api.model.container
import eu.glasskube.kubernetes.api.model.containerPort
import eu.glasskube.kubernetes.api.model.createEnv
import eu.glasskube.kubernetes.api.model.emptyDir
import eu.glasskube.kubernetes.api.model.envVar
import eu.glasskube.kubernetes.api.model.httpGet
import eu.glasskube.kubernetes.api.model.intOrString
import eu.glasskube.kubernetes.api.model.limits
import eu.glasskube.kubernetes.api.model.livenessProbe
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.kubernetes.api.model.persistentVolumeClaim
import eu.glasskube.kubernetes.api.model.readinessProbe
import eu.glasskube.kubernetes.api.model.resources
import eu.glasskube.kubernetes.api.model.secretKeyRef
import eu.glasskube.kubernetes.api.model.securityContext
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.kubernetes.api.model.volume
import eu.glasskube.kubernetes.api.model.volumeMount
import eu.glasskube.kubernetes.api.model.volumeMounts
import eu.glasskube.operator.apps.nextcloud.Nextcloud
import eu.glasskube.operator.apps.nextcloud.NextcloudReconciler
import eu.glasskube.operator.apps.nextcloud.NextcloudSmtpSpec
import eu.glasskube.operator.apps.nextcloud.appImage
import eu.glasskube.operator.apps.nextcloud.configName
import eu.glasskube.operator.apps.nextcloud.databaseEnv
import eu.glasskube.operator.apps.nextcloud.defaultEnv
import eu.glasskube.operator.apps.nextcloud.genericResourceName
import eu.glasskube.operator.apps.nextcloud.resourceLabelSelector
import eu.glasskube.operator.apps.nextcloud.resourceLabels
import eu.glasskube.operator.apps.nextcloud.volumeName
import eu.glasskube.operator.config.ConfigService
import eu.glasskube.operator.generic.condition.DeploymentReadyCondition
import io.fabric8.kubernetes.api.model.HTTPHeader
import io.fabric8.kubernetes.api.model.Quantity
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(
    labelSelector = NextcloudReconciler.SELECTOR,
    resourceDiscriminator = NextcloudDeployment.Discriminator::class
)
class NextcloudDeployment(private val configService: ConfigService) :
    CRUDKubernetesDependentResource<Deployment, Nextcloud>(Deployment::class.java) {

    class ReadyPostCondition : DeploymentReadyCondition<Nextcloud>()

    internal class Discriminator : ResourceIDMatcherDiscriminator<Deployment, Nextcloud>({
        ResourceID(it.genericResourceName, it.namespace)
    })

    override fun desired(primary: Nextcloud, context: Context<Nextcloud>) = deployment {
        metadata {
            name(primary.genericResourceName)
            namespace(primary.namespace)
            labels(primary.resourceLabels)
        }
        spec {
            selector {
                matchLabels = primary.resourceLabelSelector
            }
            replicas = 1
            strategyRecreate()
            template {
                metadata {
                    labels(primary.resourceLabels)
                    annotations(configService.getBackupAnnotations(primary, DATA_VOLUME))
                }
                spec {
                    volumes = listOfNotNull(
                        volume(TMP_VOLUME) {
                            emptyDir()
                        },
                        volume(DATA_VOLUME) {
                            persistentVolumeClaim(primary.volumeName)
                        },
                        volume(CONFIG_VOLUME) {
                            configMap(primary.configName)
                        }
                    )
                    securityContext {
                        fsGroup = 33
                    }
                    initContainers = listOf(
                        container {
                            name = "nextcloud-update"
                            image = primary.appImage
                            args = listOf("true")
                            env = primary.defaultEnv + primary.databaseEnv + adminUserEnv + primary.smtpEnv +
                                primary.storageEnv + updateControlEnv
                            volumeMounts {
                                volumeMount {
                                    name = DATA_VOLUME
                                    mountPath = DATA_DIR
                                }
                            }
                        },
                        container {
                            name = "nextcloud-apps"
                            image = primary.appImage
                            command = listOf("sh")
                            args = listOf(
                                "-c",
                                listOf(
                                    "php $OCC_PATH app:install richdocuments",
                                    "php $OCC_PATH app:install contacts",
                                    "php $OCC_PATH app:install calendar",
                                    primary.spec.apps.oidc.let {
                                        "php $OCC_PATH app:install oidc_login"
                                    },
                                    "true"
                                ).joinToString("\n") { it }
                            )
                            securityContext {
                                runAsUser = 33
                            }
                            env = primary.databaseEnv
                            volumeMounts {
                                volumeMount {
                                    name = DATA_VOLUME
                                    mountPath = DATA_DIR
                                }
                            }
                        },
                        container {
                            name = "nextcloud-config"
                            image = primary.appImage
                            command = listOf("php")
                            args = listOf(OCC_PATH, "config:import", CONFIG_FILE_PATH)
                            securityContext {
                                runAsUser = 33
                            }
                            volumeMounts {
                                volumeMount {
                                    name = DATA_VOLUME
                                    mountPath = DATA_DIR
                                }
                                volumeMount {
                                    name = CONFIG_VOLUME
                                    mountPath = CONFIG_FILE_PATH
                                    subPath = CONFIG_FILE_NAME
                                }
                            }
                        },
                        container {
                            name = "nextcloud-indices"
                            image = primary.appImage
                            command = listOf("sh")
                            args = listOf(
                                "-c",
                                """
                                    php $OCC_PATH db:add-missing-columns && \
                                    php $OCC_PATH db:add-missing-indices && \
                                    php $OCC_PATH db:add-missing-primary-keys
                                """.trimIndent()
                            )
                            securityContext {
                                runAsUser = 33
                            }
                            env = primary.databaseEnv
                            volumeMounts {
                                volumeMount {
                                    name = DATA_VOLUME
                                    mountPath = DATA_DIR
                                }
                            }
                        }
                    )
                    containers = listOf(
                        container {
                            name = Nextcloud.APP_NAME
                            image = primary.appImage
                            resources = primary.spec.server.resources
                            env =
                                primary.defaultEnv + primary.databaseEnv + primary.smtpEnv + primary.oidcEnv + primary.storageEnv
                            volumeMounts {
                                volumeMount {
                                    name = DATA_VOLUME
                                    mountPath = DATA_DIR
                                }
                                volumeMount {
                                    name = TMP_VOLUME
                                    mountPath = TMP_DIR
                                }
                                volumeMount {
                                    name = CONFIG_VOLUME
                                    mountPath = PHP_FPM_CONFIG_PATH
                                    subPath = PHP_FPM_CONFIG_FILE_NAME
                                }
                            }
                        },
                        container {
                            name = Nextcloud.NGINX_NAME
                            image = Nextcloud.NGINX_IMAGE
                            resources {
                                limits(cpu = Quantity("1", ""), memory = Quantity("200", "Mi"))
                            }
                            ports = listOf(
                                containerPort {
                                    containerPort = 80
                                    name = "http"
                                }
                            )
                            volumeMounts {
                                volumeMount {
                                    name = DATA_VOLUME
                                    mountPath = DATA_DIR
                                }
                                volumeMount {
                                    name = CONFIG_VOLUME
                                    mountPath = NGINX_CONFIG_PATH
                                    subPath = NGINX_CONFIG_FILE_NAME
                                }
                            }
                            readinessProbe {
                                periodSeconds = 10
                                successThreshold = 1
                                failureThreshold = 3
                                timeoutSeconds = 9
                                httpGet {
                                    port = intOrString("http")
                                    path = "/status.php"
                                    httpHeaders = listOf(
                                        HTTPHeader("Host", primary.spec.host)
                                    )
                                }
                            }
                            livenessProbe {
                                periodSeconds = 10
                                successThreshold = 1
                                failureThreshold = 6
                                timeoutSeconds = 9
                                httpGet {
                                    port = intOrString("http")
                                    path = "/status.php"
                                    httpHeaders = listOf(
                                        HTTPHeader("Host", primary.spec.host)
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    private val adminUserEnv
        get() = createEnv {
            envVar("NEXTCLOUD_ADMIN_USER", "root")
            envVar("NEXTCLOUD_ADMIN_PASSWORD", "glasskube-operator")
        }

    private val updateControlEnv
        get() = createEnv { envVar("NEXTCLOUD_UPDATE", "1") }

    private val Nextcloud.smtpEnv
        get() = spec.smtp?.smtpEnv.orEmpty()

    private val NextcloudSmtpSpec.smtpEnv
        get() = createEnv {
            val (mailFromAddress, mailDomain) = fromAddress.split("@", limit = 2)

            envVar("SMTP_HOST", host)
            envVar("SMTP_PORT", "$port")
            envVar("SMTP_SECURE", if (tlsEnabled) "ssl" else "")
            envVar("MAIL_FROM_ADDRESS", mailFromAddress)
            envVar("MAIL_DOMAIN", mailDomain)
            envVar("SMTP_NAME") { secretKeyRef(authSecret.name, "username") }
            envVar("SMTP_PASSWORD") { secretKeyRef(authSecret.name, "password") }
        }

    private val Nextcloud.oidcEnv
        get() = createEnv {
            spec.apps.oidc?.let { envVar("NC_oidc_login_client_id") { secretKeyRef(it.oidcSecret.name, "clientId") } }
            spec.apps.oidc?.let {
                envVar("NC_oidc_login_client_secret") {
                    secretKeyRef(
                        it.oidcSecret.name,
                        "clientSecret"
                    )
                }
            }
        }

    private val Nextcloud.storageEnv
        get() = createEnv {
            spec.storage?.s3?.apply {
                envVar("OBJECTSTORE_S3_BUCKET", bucket)
                envVar("OBJECTSTORE_S3_KEY") { secretKeyRef = accessKeySecret }
                envVar("OBJECTSTORE_S3_SECRET") { secretKeyRef = secretKeySecret }
                envVar("OBJECTSTORE_S3_SSL", useSsl.toString())
                region?.let { envVar("OBJECTSTORE_S3_REGION", it) }
                hostname?.let { envVar("OBJECTSTORE_S3_HOST", it) }
                port?.let { envVar("OBJECTSTORE_S3_PORT", it.toString()) }
                objectPrefix?.let { envVar("OBJECTSTORE_S3_OBJECT_PREFIX", objectPrefix) }
                autoCreate?.let { envVar("OBJECTSTORE_S3_AUTOCREATE", it.toString()) }
                usePathStyle?.let { envVar("OBJECTSTORE_S3_USEPATH_STYLE", it.toString()) }
                legacyAuth?.let { envVar("OBJECTSTORE_S3_LEGACYAUTH", it.toString()) }
            }
        }

    companion object {
        internal const val CONFIG_FILE_NAME = "config.json"
        internal const val NGINX_CONFIG_FILE_NAME = "nginx.conf"
        internal const val PHP_FPM_CONFIG_FILE_NAME = "zzz-glasskube-php-fpm.conf"
        private const val TMP_VOLUME = "tmp"
        private const val TMP_DIR = "/var/www/tmp"
        private const val DATA_VOLUME = "data"
        internal const val DATA_DIR = "/var/www/html"
        private const val OCC_PATH = "$DATA_DIR/occ"
        private const val CONFIG_VOLUME = "config"
        private const val CONFIG_DIR = "/glasskube/config"
        private const val CONFIG_FILE_PATH = "$CONFIG_DIR/$CONFIG_FILE_NAME"
        private const val NGINX_CONFIG_DIR = "/etc/nginx"
        private const val NGINX_CONFIG_PATH = "$NGINX_CONFIG_DIR/$NGINX_CONFIG_FILE_NAME"
        private const val PHP_FPM_CONFIG_DIR =
            "/usr/local/etc/php-fpm.d/" // php-fpm config dir is different vom php.ini config dir
        private const val PHP_FPM_CONFIG_PATH = "$PHP_FPM_CONFIG_DIR/$PHP_FPM_CONFIG_FILE_NAME"
    }
}
