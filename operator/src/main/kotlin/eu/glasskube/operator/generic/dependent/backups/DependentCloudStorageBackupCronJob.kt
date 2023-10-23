package eu.glasskube.operator.generic.dependent.backups

import eu.glasskube.kubernetes.api.model.batch.jobTemplate
import eu.glasskube.kubernetes.api.model.batch.spec
import eu.glasskube.kubernetes.api.model.batch.template
import eu.glasskube.kubernetes.api.model.container
import eu.glasskube.kubernetes.api.model.emptyDir
import eu.glasskube.kubernetes.api.model.env
import eu.glasskube.kubernetes.api.model.envVar
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.kubernetes.api.model.secretKeyRef
import eu.glasskube.kubernetes.api.model.securityContext
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.kubernetes.api.model.volume
import eu.glasskube.kubernetes.api.model.volumeMount
import eu.glasskube.kubernetes.api.model.volumeMounts
import eu.glasskube.operator.apps.common.backup.BackupSpec
import eu.glasskube.operator.apps.common.backup.ResourceWithBackups
import eu.glasskube.operator.apps.common.cloudstorage.CloudStorageSpec
import eu.glasskube.operator.apps.common.cloudstorage.ResourceWithCloudStorage
import eu.glasskube.operator.processing.CompositeAndCondition
import eu.glasskube.utils.logger
import eu.glasskube.utils.parseGolangDuration
import io.fabric8.kubernetes.api.model.Container
import io.fabric8.kubernetes.api.model.HasMetadata
import io.fabric8.kubernetes.api.model.batch.v1.CronJob
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.event.ResourceID

abstract class DependentCloudStorageBackupCronJob<P> :
    CRUDKubernetesDependentResource<CronJob, P>(CronJob::class.java)
    where P : HasMetadata, P : ResourceWithBackups, P : ResourceWithCloudStorage {

    abstract class ReconcilePrecondition<P> :
        CompositeAndCondition<CronJob, P>(CloudStorageNotNullCondition(), BackupSpecNotNullCondition())
        where P : HasMetadata, P : ResourceWithCloudStorage, P : ResourceWithBackups

    abstract class Discriminator<P> :
        ResourceIDMatcherDiscriminator<CronJob, P>({ ResourceID(it.backupResourceName, it.namespace) })
        where P : HasMetadata, P : ResourceWithCloudStorage

    override fun desired(primary: P, context: Context<P>) = CronJob().apply {
        metadata {
            name(primary.backupResourceName)
            namespace(primary.namespace)
            labels(primary.backupResourceLabels)
        }

        spec {
            val source = (primary as ResourceWithCloudStorage).getSpec().cloudStorage!!
            val backups = (primary as ResourceWithBackups).getSpec().requireBackups()

            schedule = backups.schedule
            concurrencyPolicy = "Forbid"
            jobTemplate {
                spec {
                    template {
                        spec {
                            restartPolicy = "Never"
                            volumes = listOf(
                                volume(VOLUME_NAME) { emptyDir() }
                            )
                            securityContext {
                                runAsNonRoot = true
                                runAsUser = 1009
                                runAsGroup = 1009
                                fsGroup = 1009
                            }
                            initContainers = listOf(
                                configInitContainer(SOURCE_REMOTE_NAME, source),
                                configInitContainer(DESTINATION_REMOTE_NAME, backups.s3)
                            )
                            containers = listOf(
                                container {
                                    name = "cloud-storage-backup"
                                    image = IMAGE
                                    env {
                                        envVar("SRC_REMOTE", SOURCE_REMOTE_NAME)
                                        envVar("SRC_BUCKET", source.bucket)
                                        envVar("DST_REMOTE", DESTINATION_REMOTE_NAME)
                                        envVar("DST_BUCKET", backups.s3.bucket)
                                        envVar("BACKUP_TTL", backups.ttlInSeconds.toString())
                                    }
                                    defaultVolumeMounts()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun configInitContainer(remoteName: String, spec: CloudStorageSpec) = container {
        name = "$remoteName-config"
        image = IMAGE
        env {
            envVar("ACCESS_KEY") {
                secretKeyRef(spec.accessKeySecret.name, spec.accessKeySecret.key)
            }
            envVar("SECRET_KEY") {
                secretKeyRef(spec.secretKeySecret.name, spec.secretKeySecret.key)
            }
        }
        defaultVolumeMounts()
        command = listOf("rclone")
        args = mutableListOf("config", "create", remoteName, "s3").also { argList ->
            argList += when (val endpoint = spec.endpoint) {
                null -> listOf("provider", "AWS")
                else -> listOf("provider", "Other", "endpoint", endpoint)
            }
            spec.region?.let { argList += listOf("region", it) }
            argList += listOf("access_key_id", "$(ACCESS_KEY)", "secret_access_key", "$(SECRET_KEY)")
        }
    }

    private fun Container.defaultVolumeMounts() {
        volumeMounts {
            volumeMount {
                name = VOLUME_NAME
                mountPath = VOLUME_PATH
            }
        }
    }

    private val BackupSpec.ttlInSeconds: Long
        get() = parseGolangDuration(ttl)
            .also {
                if (it.nano > 0) {
                    log.warn("fractional seconds were found in backup TTL $ttl. This is unsupported and will be ignored.")
                }
                if (it.isNegative) {
                    log.warn("negative duration will be inverted.")
                }
            }
            .abs()
            .seconds

    companion object {
        private const val VOLUME_NAME = "config"
        private const val VOLUME_PATH = "/config/rclone"
        private const val IMAGE = "ghcr.io/glasskube/cloud-storage-backup:v0.2.1"
        private const val SOURCE_REMOTE_NAME = "src"
        private const val DESTINATION_REMOTE_NAME = "dst"
        private val log = logger()
    }
}
