package eu.glasskube.operator.apps.plane.dependent

import eu.glasskube.kubernetes.api.model.apps.deployment
import eu.glasskube.kubernetes.api.model.apps.selector
import eu.glasskube.kubernetes.api.model.apps.spec
import eu.glasskube.kubernetes.api.model.apps.template
import eu.glasskube.kubernetes.api.model.configMapRef
import eu.glasskube.kubernetes.api.model.container
import eu.glasskube.kubernetes.api.model.containerPort
import eu.glasskube.kubernetes.api.model.envFrom
import eu.glasskube.kubernetes.api.model.httpGet
import eu.glasskube.kubernetes.api.model.intOrString
import eu.glasskube.kubernetes.api.model.livenessProbe
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.kubernetes.api.model.readinessProbe
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.operator.apps.plane.Plane
import eu.glasskube.operator.apps.plane.spaceImage
import eu.glasskube.operator.apps.plane.spaceResourceLabelSelector
import eu.glasskube.operator.apps.plane.spaceResourceLabels
import eu.glasskube.operator.apps.plane.spaceResourceName
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(resourceDiscriminator = PlaneSpaceDeployment.Discriminator::class)
class PlaneSpaceDeployment : CRUDKubernetesDependentResource<Deployment, Plane>(Deployment::class.java) {
    internal class Discriminator :
        ResourceIDMatcherDiscriminator<Deployment, Plane>({ ResourceID(it.spaceResourceName) })

    override fun desired(primary: Plane, context: Context<Plane>) = deployment {
        metadata {
            name = primary.spaceResourceName
            namespace = primary.namespace
            labels = primary.spaceResourceLabels
        }
        spec {
            selector {
                matchLabels = primary.spaceResourceLabelSelector
            }
            template {
                metadata {
                    labels = primary.spaceResourceLabelSelector
                }
                spec {
                    terminationGracePeriodSeconds = 10
                    containers = listOf(
                        container {
                            name = Plane.SPACE_NAME
                            image = primary.spaceImage
                            command = listOf(
                                "/usr/local/bin/start.sh",
                                "space/server.js",
                                "space"
                            )
                            envFrom {
                                configMapRef(primary.spaceResourceName)
                            }
                            ports = listOf(
                                containerPort { containerPort = 3000 }
                            )
                            resources = primary.spec.space.resources
                            readinessProbe {
                                httpGet {
                                    path = "/spaces/"
                                    port = intOrString(3000)
                                }
                            }
                            livenessProbe {
                                httpGet {
                                    path = "/spaces/"
                                    port = intOrString(3000)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
