package eu.glasskube.operator.apps.plane.dependent

import eu.glasskube.kubernetes.api.model.apps.deployment
import eu.glasskube.kubernetes.api.model.apps.selector
import eu.glasskube.kubernetes.api.model.apps.spec
import eu.glasskube.kubernetes.api.model.apps.strategyRecreate
import eu.glasskube.kubernetes.api.model.apps.template
import eu.glasskube.kubernetes.api.model.configMap
import eu.glasskube.kubernetes.api.model.configMapRef
import eu.glasskube.kubernetes.api.model.container
import eu.glasskube.kubernetes.api.model.createEnv
import eu.glasskube.kubernetes.api.model.envFrom
import eu.glasskube.kubernetes.api.model.envVar
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.kubernetes.api.model.secretRef
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.kubernetes.api.model.volume
import eu.glasskube.kubernetes.api.model.volumeMount
import eu.glasskube.kubernetes.api.model.volumeMounts
import eu.glasskube.operator.apps.plane.Plane
import eu.glasskube.operator.apps.plane.backendResourceName
import eu.glasskube.operator.apps.plane.workerResourceLabelSelector
import eu.glasskube.operator.apps.plane.workerResourceLabels
import eu.glasskube.operator.apps.plane.workerResourceName
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(resourceDiscriminator = PlaneWorkerDeployment.Discriminator::class)
class PlaneWorkerDeployment : CRUDKubernetesDependentResource<Deployment, Plane>(Deployment::class.java) {
    internal class Discriminator :
        ResourceIDMatcherDiscriminator<Deployment, Plane>({ ResourceID(it.workerResourceName) })

    override fun desired(primary: Plane, context: Context<Plane>) = deployment {
        metadata {
            name = primary.workerResourceName
            namespace = primary.namespace
            labels = primary.workerResourceLabels
        }
        spec {
            selector {
                matchLabels = primary.workerResourceLabelSelector
            }
            strategyRecreate()
            template {
                metadata {
                    labels = primary.workerResourceLabelSelector
                }
                spec {
                    volumes = listOf(
                        volume(BIN_VOLUME_NAME) { configMap(primary.workerResourceName) { defaultMode = 511 } }
                    )
                    containers = listOf(
                        container {
                            name = Plane.BACKEND_NAME
                            image = Plane.BACKEND_IMAGE
                            command = listOf(ENTRYPOINT_PATH)
                            env = primary.run { commonBackendEnv + workerEnv }
                            envFrom {
                                configMapRef(primary.backendResourceName)
                                secretRef(primary.backendResourceName)
                            }
                            volumeMounts {
                                volumeMount {
                                    name = BIN_VOLUME_NAME
                                    mountPath = BIN_VOLUME_PATH
                                    readOnly = true
                                }
                            }
                            resources = primary.spec.worker.resources
                        }
                    )
                }
            }
        }
    }

    private val Plane.workerEnv
        get() = createEnv {
            envVar("WORKERS", spec.worker.concurrency.toString())
        }

    companion object {
        internal const val BIN_VOLUME_NAME = "bin"
        private const val BIN_VOLUME_PATH = "/glasskube/bin"
        internal const val ENTRYPOINT_NAME = "entrypoint.sh"
        private const val ENTRYPOINT_PATH = "$BIN_VOLUME_PATH/$ENTRYPOINT_NAME"
    }
}
