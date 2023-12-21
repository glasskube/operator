package eu.glasskube.operator.apps.vault.dependent

import eu.glasskube.kubernetes.api.model.apps.statefulSet
import eu.glasskube.kubernetes.api.model.capabilities
import eu.glasskube.kubernetes.api.model.configMap
import eu.glasskube.kubernetes.api.model.container
import eu.glasskube.kubernetes.api.model.containerPort
import eu.glasskube.kubernetes.api.model.env
import eu.glasskube.kubernetes.api.model.envVar
import eu.glasskube.kubernetes.api.model.fieldRef
import eu.glasskube.kubernetes.api.model.lifecycle
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.kubernetes.api.model.resources
import eu.glasskube.kubernetes.api.model.secret
import eu.glasskube.kubernetes.api.model.secretKeyRef
import eu.glasskube.kubernetes.api.model.securityContext
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.kubernetes.api.model.volume
import eu.glasskube.kubernetes.api.model.volumeMount
import eu.glasskube.kubernetes.api.model.volumeMounts
import eu.glasskube.operator.apps.vault.Vault
import eu.glasskube.operator.apps.vault.Vault.Postgres.postgresDatabaseName
import eu.glasskube.operator.apps.vault.Vault.Postgres.postgresHostName
import eu.glasskube.operator.apps.vault.Vault.Postgres.postgresSecretName
import eu.glasskube.operator.apps.vault.VaultReconciler
import eu.glasskube.operator.apps.vault.appImage
import eu.glasskube.operator.apps.vault.genericResourceName
import eu.glasskube.operator.apps.vault.headlessServiceName
import eu.glasskube.operator.apps.vault.resourceLabelSelector
import eu.glasskube.operator.apps.vault.resourceLabels
import eu.glasskube.operator.apps.vault.tlsSecretName
import eu.glasskube.operator.config.ConfigService
import eu.glasskube.utils.resourceProperty
import io.fabric8.kubernetes.api.model.EnvVar
import io.fabric8.kubernetes.api.model.VolumeMount
import io.fabric8.kubernetes.api.model.apps.StatefulSet
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = VaultReconciler.SELECTOR)
class VaultStatefulSet(private val configService: ConfigService) :
    CRUDKubernetesDependentResource<StatefulSet, Vault>(StatefulSet::class.java) {

    override fun desired(primary: Vault, context: Context<Vault>) = statefulSet {
        metadata {
            name(primary.genericResourceName)
            namespace(primary.namespace)
            labels(primary.resourceLabels)
        }

        spec {
            selector { matchLabels = primary.resourceLabelSelector }

            serviceName(primary.headlessServiceName)
            replicas(primary.spec.replicas)

            primary.spec.auditStorage.takeIf { it.enabled }?.let { auditStorage ->
                volumeClaimTemplates {
                    volumeClaimTemplate {
                        metadata { name(AUDIT_VOLUME_NAME) }
                        spec {
                            resources { requests = mapOf("storage" to auditStorage.size) }
                            accessModes = listOf("ReadWriteOnce")
                        }
                    }
                }
            }

            template {
                metadata {
                    labels(primary.resourceLabels)
                    annotations(
                        if (primary.spec.auditStorage.enabled) {
                            configService.getBackupAnnotations(primary, AUDIT_VOLUME_NAME)
                        } else {
                            emptyMap()
                        }
                    )
                }

                spec {
                    serviceAccountName = primary.genericResourceName

                    volumes = listOfNotNull(
                        volume(CONFIG_VOLUME_NAME) { configMap(primary.genericResourceName) },
                        volume(TLS_VOLUME_NAME) { secret(primary.tlsSecretName) },
                        primary.spec.autoUnseal?.tlsCaSecret?.let {
                            volume(UNSEAL_TLS_VOLUME_NAME) { secret(it.name) }
                        }
                    )

                    securityContext {
                        fsGroup = 1000
                        runAsGroup = 1000
                        runAsUser = 1000
                        runAsNonRoot = true
                    }

                    terminationGracePeriodSeconds = 10

                    containers = listOf(
                        container {
                            name = Vault.APP_NAME
                            image = primary.appImage

                            ports = listOf(
                                containerPort {
                                    containerPort = 8200
                                    name = "https"
                                },
                                containerPort {
                                    containerPort = 8201
                                    name = "https-internal"
                                }
                            )

                            command = listOf("/bin/sh")
                            args = listOf("-c", if (primary.spec.autoUnseal != null) runWithAuthSh else runSh)

                            env {
                                commonEnvVars(primary)
                                postgresEnvVars(primary)
                                serviceRegistrationEnvVars(primary)
                                unsealEnvVars(primary)
                            }

                            securityContext {
                                capabilities { drop = listOf("ALL") }
                                readOnlyRootFilesystem = true
                                allowPrivilegeEscalation = false
                            }

                            lifecycle {
                                preStop {
                                    exec { command = listOf("killall", "vault") }
                                }
                            }

                            volumeMounts {
                                configVolumeMount()
                                tlsVolumeMount()
                                unsealVolumeMount(primary)
                                auditStorageVolumeMount(primary)
                            }

                            resources = primary.spec.resources
                        }
                    )
                }
            }
        }
    }

    private fun MutableList<EnvVar>.commonEnvVars(primary: Vault) {
        envVar("SKIP_SETCAP", "true")
        envVar("HOSTNAME") { fieldRef("metadata.name") }
        envVar("SUBDOMAIN", primary.headlessServiceName)
        envVar("POD_IP") { fieldRef("status.podIP") }
        envVar("VAULT_API_ADDR", "https://\$(POD_IP):8200")
        envVar("VAULT_CLUSTER_ADDR", "https://\$(HOSTNAME).\$(SUBDOMAIN):8201")
    }

    private fun MutableList<EnvVar>.postgresEnvVars(primary: Vault) {
        envVar("DB_HOST", primary.postgresHostName)
        envVar("DB_NAME", primary.postgresDatabaseName)
        envVar("DB_USERNAME") { secretKeyRef(primary.postgresSecretName, "username") }
        envVar("DB_PASSWORD") { secretKeyRef(primary.postgresSecretName, "password") }
        envVar("VAULT_PG_CONNECTION_URL", "postgres://\$(DB_USERNAME):\$(DB_PASSWORD)@\$(DB_HOST):5432/\$(DB_NAME)")
    }

    private fun MutableList<EnvVar>.serviceRegistrationEnvVars(primary: Vault) {
        if (primary.spec.serviceRegistration.enabled) {
            envVar("VAULT_K8S_POD_NAME") { fieldRef("metadata.name") }
            envVar("VAULT_K8S_NAMESPACE") { fieldRef("metadata.namespace") }
        }
    }

    private fun MutableList<EnvVar>.unsealEnvVars(primary: Vault) {
        primary.spec.autoUnseal?.apply {
            envVar("VAULT_ADDR", address)
            tlsCaSecret?.let { envVar("VAULT_CACERT", "$UNSEAL_TLS_VOLUME_DIR/${it.key}") }
            envVar("VAULT_TRANSIT_SEAL_MOUNT_PATH", mountPath)
            envVar("AUTH_PATH", authPath)
            val defaultKeyAndRoleName = "${primary.namespace}.${primary.metadata.name}"
            envVar("VAULT_TRANSIT_SEAL_KEY_NAME", keyName ?: defaultKeyAndRoleName)
            envVar("AUTH_ROLE_NAME", roleName ?: defaultKeyAndRoleName)
        }
    }

    private fun MutableList<VolumeMount>.configVolumeMount() {
        volumeMount {
            name = CONFIG_VOLUME_NAME
            mountPath = CONFIG_VOLUME_DIR
            readOnly = true
        }
    }

    private fun MutableList<VolumeMount>.tlsVolumeMount() {
        volumeMount {
            name = TLS_VOLUME_NAME
            mountPath = TLS_VOLUME_DIR
            readOnly = true
        }
    }

    private fun MutableList<VolumeMount>.unsealVolumeMount(primary: Vault) {
        primary.spec.autoUnseal?.tlsCaSecret?.let {
            volumeMount {
                name = UNSEAL_TLS_VOLUME_NAME
                mountPath = "$UNSEAL_TLS_VOLUME_DIR/${it.key}"
                subPath = it.key
                readOnly = true
            }
        }
    }

    private fun MutableList<VolumeMount>.auditStorageVolumeMount(primary: Vault) {
        if (primary.spec.auditStorage.enabled) {
            volumeMount {
                name = AUDIT_VOLUME_NAME
                mountPath = AUDIT_VOLUME_DIR
            }
        }
    }

    private val runSh by resourceProperty()
    private val runWithAuthSh by resourceProperty()

    companion object {
        private const val CONFIG_VOLUME_NAME = "config"
        private const val CONFIG_VOLUME_DIR = "/glasskube/config"
        const val CONFIG_FILE_NAME = "config.hcl"
        private const val AUDIT_VOLUME_NAME = "audit"
        private const val AUDIT_VOLUME_DIR = "/vault/audit"
        private const val UNSEAL_TLS_VOLUME_NAME = "unseal"
        private const val UNSEAL_TLS_VOLUME_DIR = "/glasskube/unseal/"
        private const val TLS_VOLUME_NAME = "tls"
        private const val TLS_VOLUME_DIR = "/glasskube/tls"
    }
}
