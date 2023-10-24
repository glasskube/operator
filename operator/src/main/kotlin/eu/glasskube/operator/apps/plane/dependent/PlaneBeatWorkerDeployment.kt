package eu.glasskube.operator.apps.plane.dependent

import eu.glasskube.kubernetes.api.model.apps.deployment
import eu.glasskube.kubernetes.api.model.apps.selector
import eu.glasskube.kubernetes.api.model.apps.spec
import eu.glasskube.kubernetes.api.model.apps.strategyRecreate
import eu.glasskube.kubernetes.api.model.apps.template
import eu.glasskube.kubernetes.api.model.configMapRef
import eu.glasskube.kubernetes.api.model.container
import eu.glasskube.kubernetes.api.model.envFrom
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.kubernetes.api.model.secretRef
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.operator.apps.plane.Plane
import eu.glasskube.operator.apps.plane.backendImage
import eu.glasskube.operator.apps.plane.backendResourceName
import eu.glasskube.operator.apps.plane.beatWorkerResourceLabelSelector
import eu.glasskube.operator.apps.plane.beatWorkerResourceLabels
import eu.glasskube.operator.apps.plane.beatWorkerResourceName
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(resourceDiscriminator = PlaneBeatWorkerDeployment.Discriminator::class)
class PlaneBeatWorkerDeployment : CRUDKubernetesDependentResource<Deployment, Plane>(Deployment::class.java) {

    internal class Discriminator :
        ResourceIDMatcherDiscriminator<Deployment, Plane>({ ResourceID(it.beatWorkerResourceName, it.namespace) })

    override fun desired(primary: Plane, context: Context<Plane>) = deployment {
        metadata {
            name(primary.beatWorkerResourceName)
            namespace(primary.namespace)
            labels(primary.beatWorkerResourceLabels)
        }
        spec {
            selector {
                matchLabels = primary.beatWorkerResourceLabelSelector
            }
            strategyRecreate()
            template {
                metadata {
                    labels(primary.beatWorkerResourceLabelSelector)
                }
                spec {
                    containers = listOf(
                        container {
                            name = Plane.BACKEND_NAME
                            image = primary.backendImage
                            command = listOf("./bin/beat")
                            env = primary.commonBackendEnv
                            envFrom {
                                configMapRef(primary.backendResourceName)
                                secretRef(primary.backendResourceName)
                            }
                            resources = primary.spec.beatWorker.resources
                        }
                    )
                }
            }
        }
    }
}
