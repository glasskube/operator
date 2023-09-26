package eu.glasskube.operator.apps.matomo.dependent

import eu.glasskube.kubernetes.api.model.batch.jobTemplate
import eu.glasskube.kubernetes.api.model.batch.spec
import eu.glasskube.kubernetes.api.model.batch.template
import eu.glasskube.kubernetes.api.model.configMapRef
import eu.glasskube.kubernetes.api.model.container
import eu.glasskube.kubernetes.api.model.envFrom
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.kubernetes.api.model.persistentVolumeClaim
import eu.glasskube.kubernetes.api.model.secretRef
import eu.glasskube.kubernetes.api.model.securityContext
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.kubernetes.api.model.volume
import eu.glasskube.kubernetes.api.model.volumeMount
import eu.glasskube.kubernetes.api.model.volumeMounts
import eu.glasskube.operator.Affinities
import eu.glasskube.operator.apps.matomo.Matomo
import eu.glasskube.operator.apps.matomo.MatomoReconciler
import eu.glasskube.operator.apps.matomo.appImage
import eu.glasskube.operator.apps.matomo.configMapName
import eu.glasskube.operator.apps.matomo.cronName
import eu.glasskube.operator.apps.matomo.databaseSecretName
import eu.glasskube.operator.apps.matomo.resourceLabels
import eu.glasskube.operator.apps.matomo.volumeName
import io.fabric8.kubernetes.api.model.batch.v1.CronJob
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = MatomoReconciler.SELECTOR)
class MatomoCronJob : CRUDKubernetesDependentResource<CronJob, Matomo>(CronJob::class.java) {
    override fun desired(primary: Matomo, context: Context<Matomo>) = CronJob().apply {
        metadata {
            name = primary.cronName
            namespace = primary.namespace
            labels = primary.resourceLabels
        }
        spec {
            schedule = "5 * * * *"
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
                                    name = Matomo.APP_NAME
                                    image = primary.appImage
                                    command = listOf("/var/www/html/console")
                                    args = listOf("core:archive", "--url=https://${primary.spec.host}")
                                    envFrom {
                                        secretRef(primary.databaseSecretName)
                                        configMapRef(primary.configMapName)
                                    }
                                    securityContext {
                                        runAsUser = 33
                                    }
                                    volumeMounts {
                                        volumeMount {
                                            name = DATA_VOLUME
                                            mountPath = MatomoDeployment.htmlDir
                                        }
                                    }
                                }
                            )
                            affinity = Affinities.podAffinityFor(primary.resourceLabels)
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
