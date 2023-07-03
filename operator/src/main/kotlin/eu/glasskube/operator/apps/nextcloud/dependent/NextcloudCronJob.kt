package eu.glasskube.operator.apps.nextcloud.dependent

import eu.glasskube.kubernetes.api.model.batch.jobTemplate
import eu.glasskube.kubernetes.api.model.batch.spec
import eu.glasskube.kubernetes.api.model.batch.template
import eu.glasskube.kubernetes.api.model.container
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.kubernetes.api.model.persistentVolumeClaim
import eu.glasskube.kubernetes.api.model.securityContext
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.kubernetes.api.model.volume
import eu.glasskube.kubernetes.api.model.volumeMount
import eu.glasskube.kubernetes.api.model.volumeMounts
import eu.glasskube.operator.apps.nextcloud.Nextcloud
import eu.glasskube.operator.apps.nextcloud.NextcloudReconciler
import eu.glasskube.operator.apps.nextcloud.databaseEnv
import eu.glasskube.operator.apps.nextcloud.defaultEnv
import eu.glasskube.operator.apps.nextcloud.genericResourceName
import eu.glasskube.operator.apps.nextcloud.resourceLabels
import eu.glasskube.operator.apps.nextcloud.volumeName
import io.fabric8.kubernetes.api.model.batch.v1.CronJob
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = NextcloudReconciler.SELECTOR)
class NextcloudCronJob : CRUDKubernetesDependentResource<CronJob, Nextcloud>(CronJob::class.java) {
    override fun desired(primary: Nextcloud, context: Context<Nextcloud>) = CronJob().apply {
        metadata {
            name = primary.genericResourceName
            namespace = primary.namespace
            labels = primary.resourceLabels
        }
        spec {
            schedule = "*/5 * * * *"
            jobTemplate {
                spec {
                    template {
                        spec {
                            restartPolicy = "Never"
                            volumes = listOf(
                                volume(DATA_VOLUME) {
                                    persistentVolumeClaim(primary.volumeName)
                                }
                            )
                            containers = listOf(
                                container {
                                    name = Nextcloud.APP_NAME
                                    image = Nextcloud.APP_IMAGE
                                    command = listOf("php")
                                    args = listOf("cron.php")
                                    env = primary.defaultEnv + primary.databaseEnv
                                    securityContext {
                                        runAsUser = 33
                                    }
                                    volumeMounts {
                                        volumeMount {
                                            name = DATA_VOLUME
                                            mountPath = NextcloudDeployment.DATA_DIR
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val DATA_VOLUME = "data"
    }
}
