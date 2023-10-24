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
import eu.glasskube.kubernetes.api.model.emptyDir
import eu.glasskube.kubernetes.api.model.env
import eu.glasskube.kubernetes.api.model.envFrom
import eu.glasskube.kubernetes.api.model.envVar
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.kubernetes.api.model.persistentVolumeClaim
import eu.glasskube.kubernetes.api.model.secretKeyRef
import eu.glasskube.kubernetes.api.model.secretRef
import eu.glasskube.kubernetes.api.model.securityContext
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.kubernetes.api.model.volume
import eu.glasskube.kubernetes.api.model.volumeMount
import eu.glasskube.kubernetes.api.model.volumeMounts
import eu.glasskube.operator.Affinities
import eu.glasskube.operator.apps.glitchtip.Glitchtip
import eu.glasskube.operator.apps.glitchtip.Glitchtip.Postgres.postgresSecretName
import eu.glasskube.operator.apps.glitchtip.GlitchtipReconciler
import eu.glasskube.operator.apps.glitchtip.appImage
import eu.glasskube.operator.apps.glitchtip.configMapName
import eu.glasskube.operator.apps.glitchtip.genericResourceName
import eu.glasskube.operator.apps.glitchtip.resourceLabelSelector
import eu.glasskube.operator.apps.glitchtip.resourceLabels
import eu.glasskube.operator.apps.glitchtip.secretName
import eu.glasskube.operator.apps.glitchtip.workerName
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(
    labelSelector = GlitchtipReconciler.SELECTOR,
    resourceDiscriminator = GlitchtipWorkerDeployment.Discriminator::class
)
class GlitchtipWorkerDeployment : CRUDKubernetesDependentResource<Deployment, Glitchtip>(Deployment::class.java) {

    internal class Discriminator : ResourceIDMatcherDiscriminator<Deployment, Glitchtip>({
        ResourceID(it.workerName, it.namespace)
    })

    override fun desired(primary: Glitchtip, context: Context<Glitchtip>) = deployment {
        metadata {
            name(primary.workerName)
            namespace(primary.metadata.namespace)
            labels(primary.resourceLabels)
        }
        spec {
            selector {
                matchLabels = primary.resourceLabelSelector
            }
            replicas = primary.spec.replicas
            if (primary.spec.replicas > 1) strategyRollingUpdate() else strategyRecreate()
            template {
                metadata {
                    labels(primary.resourceLabels)
                }
                spec {
                    containers = listOf(
                        container {
                            name = Glitchtip.APP_NAME
                            image = primary.appImage
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
                                envVar("SERVER_ROLE", "worker_with_beat")
                                primary.spec.smtp?.also {
                                    envVar("SMTP_USERNAME") {
                                        secretKeyRef(it.authSecret.name, "username")
                                    }
                                    envVar("SMTP_PASSWORD") {
                                        secretKeyRef(it.authSecret.name, "password")
                                    }

                                    envVar(
                                        "EMAIL_URL",
                                        "smtp://\$(SMTP_USERNAME):\$(SMTP_PASSWORD)@${it.host}:${it.port}"
                                    )
                                    envVar("DEFAULT_FROM_EMAIL", it.fromAddress)
                                }
                                if (primary.spec.smtp == null) {
                                    envVar("EMAIL_URL", "consolemail://")
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
        private const val TMP_VOLUME = "tmp"
        private const val TMP_DIR = "/tmp"
    }
}
