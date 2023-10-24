package eu.glasskube.operator.apps.plane.dependent

import eu.glasskube.kubernetes.api.model.apps.deployment
import eu.glasskube.kubernetes.api.model.apps.selector
import eu.glasskube.kubernetes.api.model.apps.spec
import eu.glasskube.kubernetes.api.model.apps.template
import eu.glasskube.kubernetes.api.model.configMap
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
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.kubernetes.api.model.readinessProbe
import eu.glasskube.kubernetes.api.model.secretRef
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.kubernetes.api.model.volume
import eu.glasskube.kubernetes.api.model.volumeMount
import eu.glasskube.kubernetes.api.model.volumeMounts
import eu.glasskube.operator.apps.plane.Plane
import eu.glasskube.operator.apps.plane.apiResourceLabelSelector
import eu.glasskube.operator.apps.plane.apiResourceLabels
import eu.glasskube.operator.apps.plane.apiResourceName
import eu.glasskube.operator.apps.plane.backendImage
import eu.glasskube.operator.apps.plane.backendResourceName
import eu.glasskube.operator.generic.condition.DeploymentReadyCondition
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(resourceDiscriminator = PlaneApiDeployment.Discriminator::class)
class PlaneApiDeployment : CRUDKubernetesDependentResource<Deployment, Plane>(Deployment::class.java) {

    internal class Discriminator :
        ResourceIDMatcherDiscriminator<Deployment, Plane>({ ResourceID(it.apiResourceName, it.namespace) })

    internal class ReadyCondition : DeploymentReadyCondition<Plane>()

    override fun desired(primary: Plane, context: Context<Plane>) = deployment {
        metadata {
            name(primary.apiResourceName)
            namespace(primary.namespace)
            labels(primary.apiResourceLabels)
        }
        spec {
            selector {
                matchLabels = primary.apiResourceLabelSelector
            }
            template {
                metadata {
                    labels(primary.apiResourceLabelSelector)
                }
                spec {
                    volumes = listOf(
                        volume(BIN_VOLUME_NAME) { configMap(primary.apiResourceName) { defaultMode = 511 } }
                    )
                    containers = listOf(
                        container {
                            name = Plane.BACKEND_NAME
                            image = primary.backendImage
                            command = listOf(ENTRYPOINT_PATH)
                            env = primary.run { commonBackendEnv + apiEnv }
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
                            ports = listOf(
                                containerPort { containerPort = 8000 }
                            )
                            resources = primary.spec.api.resources
                            readinessProbe {
                                httpGet {
                                    path = "/"
                                    port = intOrString(8000)
                                }
                            }
                            livenessProbe {
                                failureThreshold = 6
                                httpGet {
                                    path = "/"
                                    port = intOrString(8000)
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    private val Plane.apiEnv
        get() = createEnv {
            envVar("BACKEND_WORKERS", spec.api.concurrency.toString())
        }

    companion object {
        internal const val BIN_VOLUME_NAME = "bin"
        private const val BIN_VOLUME_PATH = "/glasskube/bin"
        internal const val ENTRYPOINT_NAME = "entrypoint.sh"
        private const val ENTRYPOINT_PATH = "$BIN_VOLUME_PATH/$ENTRYPOINT_NAME"
    }
}
